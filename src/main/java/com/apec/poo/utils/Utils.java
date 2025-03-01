package com.apec.poo.utils;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class Utils {

    public static void showErrorMessage(String message) {
        showMessage(message, NotificationVariant.LUMO_ERROR);
    }

    public static void showInfoMessage(String message) {
        showMessage(message, NotificationVariant.LUMO_SUCCESS);
    }

    public static void showWarningMessage(String message) {
        showMessage(message, NotificationVariant.LUMO_WARNING);
    }

    public static void showMessage(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
