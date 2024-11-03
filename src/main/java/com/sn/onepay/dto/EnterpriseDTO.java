package com.sn.onepay.dto;

import com.sn.onepay.enumeration.Modules;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Collection;

public record EnterpriseDTO(

        @Schema(name = "id", description = "Enterprise identifier")
        Long id,

        @Schema(name = "ref", description = "Enterprise reference")
        String ref,

        @Schema(name = "name", description = "Enterprise name")
        @NotNull(message = "Enterprise name can not be null")
        String name,

        @Schema(name = "maxQuota", description = "Max quota of users by enterprise")
        @NotNull(message = "Enterprise quota can not be null")
        Long maxQuota,

        @Schema(name = "actualQuota", description = "Actual quota of users by enterprise")
        Long actualQuota,

        @Schema(name = "address", description = "Address of the enterprise")
        String address,

        @Schema(name = "enrolledModules", description = "List of modules enrolled by the enterprise")
        Collection<Modules> enrolledModules,

        @Schema(name = "creationDate", description = "Enterprise date of creation")
        LocalDateTime creationDate,

        @Schema(name = "modificationDate", description = "Enterprise date of modification")
        LocalDateTime modificationDate
) {
}
