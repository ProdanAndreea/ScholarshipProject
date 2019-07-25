package com.siemens;

import com.siemens.controller.ClientController;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description:
 *   Starting point for the Client application.
 */
public class ClientStart {

    private ClientStart() {
    }

    public static void main(String[] args) {
        new ClientController();
    }
}
