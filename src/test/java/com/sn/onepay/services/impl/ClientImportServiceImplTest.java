package com.sn.onepay.services.impl;

import com.sn.onepay.dto.ClientCreateDTO;
import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.dto.ClientImportReportDTO.CreatedRow;
import com.sn.onepay.dto.ClientImportReportDTO.RowError;
import com.sn.onepay.dto.EnterpriseProfileDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.InvalidFileException;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.services.ClientService;
import com.sn.onepay.services.EnterpriseProfileService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ClientImportServiceImplTest {

    static final Object[] HEADER = {"Prénom", "Nom", "Username", "Email", "Téléphone"};

    ClientService clientService;
    EnterpriseProfileService enterpriseProfileService;
    ClientImportServiceImpl importService;

    @BeforeEach
    void setUp() {
        clientService = mock(ClientService.class);
        enterpriseProfileService = mock(EnterpriseProfileService.class);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        importService = new ClientImportServiceImpl(clientService, enterpriseProfileService, validator);
        when(clientService.createClient(any())).thenAnswer(invocation -> {
            ClientCreateDTO dto = invocation.getArgument(0);
            return new ClientDTO(100L, null, dto.firstname(), dto.lastname(), dto.username(), dto.email(), dto.phoneNumber(),
                    Roles.CLIENT, null, true, null, null);
        });
    }

    @Test
    void importClients_createsValidRowsAndReportsInvalidOnes() throws IOException {
        var file = xlsx(
                HEADER,
                new Object[]{"Awa", "Ndiaye", "awa.ndiaye", "awa@mail.sn", 771234567d},
                new Object[]{"Moussa", "Diop", "moussa.diop", "moussa@mail.sn", "12345"},
                new Object[]{},
                new Object[]{"", "Fall", "fall", "pas-un-email", "+221 70 000 00 00"},
                new Object[]{"Fatou", "Sow", "fatou.sow", "fatou@mail.sn", " 77 765 43 21 "});

        var report = importService.importClients(file, 9L, "super.admin", true);

        assertThat(report.totalRows()).isEqualTo(4);
        assertThat(report.created()).containsExactly(new CreatedRow(2, 100L, "awa.ndiaye"), new CreatedRow(6, 100L, "fatou.sow"));
        assertThat(report.errors()).containsExactly(
                new RowError(3, "Téléphone : Numéro de téléphone sénégalais attendu : 9 chiffres commençant par 7 (ex. 771234567 ou +221 77 123 45 67)"),
                new RowError(5, "Prénom : obligatoire ; Email : adresse e-mail invalide"));
        verify(clientService).createClient(new ClientCreateDTO("Awa", "Ndiaye", "awa.ndiaye", "awa@mail.sn", "771234567", Roles.CLIENT, 9L));
        verify(clientService).createClient(new ClientCreateDTO("Fatou", "Sow", "fatou.sow", "fatou@mail.sn", "77 765 43 21", Roles.CLIENT, 9L));
    }

    @Test
    void importClients_reportsMissingRequiredColumns() throws IOException {
        var file = xlsx(HEADER, new Object[]{"Awa", "", "", "", ""});

        var report = importService.importClients(file, 9L, "super.admin", true);

        assertThat(report.errors()).containsExactly(new RowError(2, "Nom : obligatoire ; Username : obligatoire ; Email : obligatoire ; Téléphone : obligatoire"));
        verify(clientService, never()).createClient(any());
    }

    @Test
    void importClients_reportsBusinessErrorsOfCreation() throws IOException {
        doThrow(new ResourceAlreadyExistException("Cet e-mail est déjà utilisé par un autre compte : awa@mail.sn"))
                .doThrow(new IllegalStateException("boom"))
                .when(clientService).createClient(any());
        var file = xlsx(HEADER,
                new Object[]{"Awa", "Ndiaye", "awa.ndiaye", "awa@mail.sn", "771234567"},
                new Object[]{"Moussa", "Diop", "moussa.diop", "moussa@mail.sn", "772222222"});

        var report = importService.importClients(file, 9L, "super.admin", true);

        assertThat(report.created()).isEmpty();
        assertThat(report.errors()).containsExactly(
                new RowError(2, "Cet e-mail est déjà utilisé par un autre compte : awa@mail.sn"),
                new RowError(3, "Erreur inattendue lors de la création de cette ligne"));
    }

    @Test
    void importClients_enterpriseAdminAlwaysImportsIntoOwnEnterprise() throws IOException {
        Enterprise enterprise = new Enterprise();
        enterprise.setId(4L);
        EnterpriseProfileDTO profile = mock(EnterpriseProfileDTO.class);
        when(profile.enterprise()).thenReturn(enterprise);
        when(enterpriseProfileService.getMyProfile("enterprise.admin")).thenReturn(profile);
        var file = xlsx(HEADER, new Object[]{"Awa", "Ndiaye", "awa.ndiaye", "awa@mail.sn", "771234567"});

        importService.importClients(file, 99L, "enterprise.admin", false);

        verify(clientService).createClient(new ClientCreateDTO("Awa", "Ndiaye", "awa.ndiaye", "awa@mail.sn", "771234567", Roles.CLIENT, 4L));
    }

    @Test
    void importClients_superAdminMustProvideEnterprise() throws IOException {
        var file = xlsx(HEADER);

        assertThatThrownBy(() -> importService.importClients(file, null, "super.admin", true))
                .isInstanceOf(ObjectValidationException.class)
                .hasMessage("L'entreprise (enterpriseId) est obligatoire pour un import par le SUPER_ADMIN");
        verifyNoInteractions(clientService);
    }

    @Test
    void importClients_rejectsWrongHeaders() throws IOException {
        var file = xlsx(new Object[]{"Nom", "Prénom", "Username", "Email", "Téléphone"});

        assertThatThrownBy(() -> importService.importClients(file, 9L, "super.admin", true))
                .isInstanceOf(InvalidFileException.class)
                .hasMessage("En-têtes attendus en ligne 1 : Prénom | Nom | Username | Email | Téléphone");
    }

    @Test
    void importClients_rejectsEmptySheet() throws IOException {
        var file = xlsx();

        assertThatThrownBy(() -> importService.importClients(file, 9L, "super.admin", true))
                .isInstanceOf(InvalidFileException.class);
    }

    @Test
    void importClients_rejectsFileThatIsNotXlsx() {
        var file = new MockMultipartFile("file", "employes.csv", "text/csv", "Prénom;Nom\nAwa;Ndiaye".getBytes());

        assertThatThrownBy(() -> importService.importClients(file, 9L, "super.admin", true))
                .isInstanceOf(InvalidFileException.class)
                .hasMessage("Fichier Excel (.xlsx) illisible");
    }

    @Test
    void importClients_headersAreCaseInsensitive() throws IOException {
        var file = xlsx(new Object[]{"prénom", "NOM", "username", "EMAIL", "téléphone"}, new Object[]{"Awa", "Ndiaye", "awa.ndiaye", "awa@mail.sn", "771234567"});

        var report = importService.importClients(file, 9L, "super.admin", true);

        assertThat(report.created()).hasSize(1);
        assertThat(report.errors()).isEqualTo(List.of());
    }

    private static MockMultipartFile xlsx(Object[]... rows) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Employés");
            for (int r = 0; r < rows.length; r++) {
                if (rows[r].length == 0) {
                    continue; // ligne absente du fichier
                }
                Row row = sheet.createRow(r);
                for (int c = 0; c < rows[r].length; c++) {
                    if (rows[r][c] instanceof Double number) {
                        row.createCell(c).setCellValue(number);
                    } else {
                        row.createCell(c).setCellValue((String) rows[r][c]);
                    }
                }
            }
            workbook.write(out);
            return new MockMultipartFile("file", "employes.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());
        }
    }
}
