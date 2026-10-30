package com.sn.onepay.services.impl;

import com.sn.onepay.dto.ClientCreateDTO;
import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.dto.ClientImportReportDTO;
import com.sn.onepay.dto.ClientImportReportDTO.CreatedRow;
import com.sn.onepay.dto.ClientImportReportDTO.RowError;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.InvalidFileException;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.keycloak.KeycloakUnavailableException;
import com.sn.onepay.services.ClientImportService;
import com.sn.onepay.services.ClientService;
import com.sn.onepay.services.EnterpriseProfileService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*Pas de @Transactional ici : chaque ligne est créée dans sa propre transaction (ClientService),
  une ligne en erreur n'annule donc pas les autres*/
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientImportServiceImpl implements ClientImportService {

    static final List<String> HEADERS = List.of("Prénom", "Nom", "Username", "Email", "Téléphone");
    static final Map<String, String> COLUMN_LABELS = Map.of(
            "firstname", "Prénom", "lastname", "Nom", "username", "Username", "email", "Email", "phoneNumber", "Téléphone");

    final ClientService clientService;
    final EnterpriseProfileService enterpriseProfileService;
    final Validator validator;
    final DataFormatter formatter = new DataFormatter();

    @Override
    public ClientImportReportDTO importClients(MultipartFile file, Long enterpriseId, String callerUsername, boolean callerIsSuperAdmin) {
        Long targetEnterpriseId = resolveEnterprise(enterpriseId, callerUsername, callerIsSuperAdmin);

        List<CreatedRow> created = new ArrayList<>();
        List<RowError> errors = new ArrayList<>();
        int totalRows = 0;

        try (InputStream input = file.getInputStream(); Workbook workbook = new XSSFWorkbook(input)) {
            Sheet sheet = workbook.getSheetAt(0);
            checkHeaders(sheet.getRow(0));

            for (int index = 1; index <= sheet.getLastRowNum(); index++) {
                List<String> values = readRow(sheet.getRow(index));
                if (values.stream().allMatch(String::isEmpty)) {
                    continue;
                }
                totalRows++;
                int excelRow = index + 1;
                ClientCreateDTO client = new ClientCreateDTO(cell(values, 0), cell(values, 1), cell(values, 2), cell(values, 3), cell(values, 4),
                        Roles.CLIENT, targetEnterpriseId);
                String invalid = validate(client);
                if (invalid != null) {
                    errors.add(new RowError(excelRow, invalid));
                    continue;
                }
                try {
                    ClientDTO saved = clientService.createClient(client);
                    created.add(new CreatedRow(excelRow, saved.id(), saved.username()));
                } catch (ResourceAlreadyExistException | ObjectValidationException | ResourceNotFoundException | KeycloakUnavailableException exception) {
                    errors.add(new RowError(excelRow, exception.getMessage()));
                } catch (RuntimeException exception) {
                    log.error("Unexpected error while importing row {}: {}", excelRow, exception.getMessage());
                    errors.add(new RowError(excelRow, "Erreur inattendue lors de la création de cette ligne"));
                }
            }
        } catch (IOException | RuntimeException exception) {
            if (exception instanceof InvalidFileException invalidFile) {
                throw invalidFile;
            }
            throw new InvalidFileException("Fichier Excel (.xlsx) illisible");
        }

        log.info("Imported {} client(s) into enterprise {}, {} row(s) rejected", created.size(), targetEnterpriseId, errors.size());
        return new ClientImportReportDTO(totalRows, created, errors);
    }

    private Long resolveEnterprise(Long enterpriseId, String callerUsername, boolean callerIsSuperAdmin) {
        if (callerIsSuperAdmin) {
            if (enterpriseId == null) {
                throw new ObjectValidationException("L'entreprise (enterpriseId) est obligatoire pour un import par le SUPER_ADMIN");
            }
            return enterpriseId;
        }
        // ENTERPRISE_ADMIN : toujours sa propre entreprise, quel que soit l'enterpriseId envoyé
        return enterpriseProfileService.getMyProfile(callerUsername).enterprise().getId();
    }

    private void checkHeaders(Row header) {
        List<String> actual = readRow(header);
        for (int i = 0; i < HEADERS.size(); i++) {
            if (!HEADERS.get(i).equalsIgnoreCase(actual.get(i))) {
                throw new InvalidFileException("En-têtes attendus en ligne 1 : " + String.join(" | ", HEADERS));
            }
        }
    }

    private List<String> readRow(Row row) {
        List<String> values = new ArrayList<>(HEADERS.size());
        for (int column = 0; column < HEADERS.size(); column++) {
            values.add(row == null ? "" : formatter.formatCellValue(row.getCell(column)).trim());
        }
        return values;
    }

    /*Cellule vide → null, pour que les contraintes @NotNull/@NotBlank du DTO la signalent*/
    private static String cell(List<String> values, int column) {
        String value = values.get(column);
        return value.isEmpty() ? null : value;
    }

    /*Prénom et nom sont obligatoires en base ; les autres règles viennent des annotations de ClientCreateDTO*/
    private String validate(ClientCreateDTO client) {
        List<String> messages = new ArrayList<>();
        if (client.firstname() == null) {
            messages.add("Prénom : obligatoire");
        }
        if (client.lastname() == null) {
            messages.add("Nom : obligatoire");
        }
        Set<ConstraintViolation<ClientCreateDTO>> violations = validator.validate(client);
        violations.stream()
                .sorted(Comparator.comparing(violation -> HEADERS.indexOf(label(violation))))
                .map(violation -> label(violation) + " : " + message(violation))
                .distinct()
                .forEach(messages::add);
        return messages.isEmpty() ? null : messages.stream().collect(Collectors.joining(" ; "));
    }

    private static String label(ConstraintViolation<ClientCreateDTO> violation) {
        String field = violation.getPropertyPath().toString();
        return COLUMN_LABELS.getOrDefault(field, field);
    }

    private static String message(ConstraintViolation<ClientCreateDTO> violation) {
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Email) {
            return "adresse e-mail invalide";
        }
        if (violation.getInvalidValue() == null) {
            return "obligatoire";
        }
        return violation.getMessage();
    }
}
