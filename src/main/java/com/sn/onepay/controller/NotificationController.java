package com.sn.onepay.controller;

import com.sn.onepay.dto.DeviceTokenDTO;
import com.sn.onepay.dto.NotificationDTO;
import com.sn.onepay.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/*Notifications de l'utilisateur connecté uniquement : le username vient toujours du token, jamais de la requête*/
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping(value = "/v1/onepay/notification")
@RequiredArgsConstructor
public class NotificationController {

    final NotificationService notificationService;

    @Operation(summary = "Register the device token of the connected user",
            description = "Firebase Cloud Messaging token of the mobile device. Call it after login and whenever the token is refreshed. A token already registered is re-attached to the connected user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registered"),
            @ApiResponse(responseCode = "400", description = "Incorrect request")
    })
    @PostMapping(value = "/device-token", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registerDeviceToken(@RequestBody @Valid DeviceTokenDTO deviceToken, @AuthenticationPrincipal Jwt jwt) {
        notificationService.registerDeviceToken(username(jwt), deviceToken);
    }

    @Operation(summary = "Unregister a device token of the connected user", description = "Call it on logout so the device stops receiving the user's notifications")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Unregistered (or unknown token)")})
    @DeleteMapping("/device-token/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterDeviceToken(@Parameter(description = "FCM token", required = true) @PathVariable String token, @AuthenticationPrincipal Jwt jwt) {
        notificationService.unregisterDeviceToken(username(jwt), token);
    }

    @Operation(summary = "Get the notifications of the connected user", description = "Most recent first; optionally only read or unread ones")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<NotificationDTO> getMyNotifications(@Parameter(description = "Filter by read status") @RequestParam(required = false) Boolean read,
                                                    @PageableDefault(sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable,
                                                    @AuthenticationPrincipal Jwt jwt) {
        return notificationService.getMyNotifications(username(jwt), read, pageable);
    }

    @Operation(summary = "Count the unread notifications of the connected user", description = "Badge of the notification bell")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping("/unread-count")
    @ResponseStatus(HttpStatus.OK)
    public long countUnread(@AuthenticationPrincipal Jwt jwt) {
        return notificationService.countUnread(username(jwt));
    }

    @Operation(summary = "Mark a notification as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found, or notification of another user")
    })
    @PutMapping("/{notificationId}/read")
    @ResponseStatus(HttpStatus.OK)
    public NotificationDTO markAsRead(@Parameter(description = "Notification id", required = true) @PathVariable Long notificationId,
                                      @AuthenticationPrincipal Jwt jwt) {
        return notificationService.markAsRead(notificationId, username(jwt));
    }

    @Operation(summary = "Mark all the notifications of the connected user as read", description = "Returns the number of notifications marked as read")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success")})
    @PutMapping("/read-all")
    @ResponseStatus(HttpStatus.OK)
    public int markAllAsRead(@AuthenticationPrincipal Jwt jwt) {
        return notificationService.markAllAsRead(username(jwt));
    }

    private static String username(Jwt jwt) {
        return jwt == null ? null : jwt.getClaimAsString("preferred_username");
    }
}
