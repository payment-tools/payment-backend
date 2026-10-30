package com.sn.onepay.services;

import com.sn.onepay.dto.ClientImportReportDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ClientImportService {

    /*Importe les employés d'un fichier .xlsx (Prénom | Nom | Username | Email | Téléphone).
      ENTERPRISE_ADMIN : toujours dans sa propre entreprise ; SUPER_ADMIN : enterpriseId obligatoire.
      Les lignes valides sont créées, les autres reportées en erreur.*/
    ClientImportReportDTO importClients(MultipartFile file, Long enterpriseId, String callerUsername, boolean callerIsSuperAdmin);
}
