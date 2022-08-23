/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_producto;

import controlador.ControllerLogin;
import modelo.UsuarioJpaController;
import View.View_Login;

/**
 *
 * @author danny
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ManageFactory manager = new ManageFactory();
        View_Login vista = new View_Login();
        UsuarioJpaController modelo = new UsuarioJpaController(manager.getEntityManagerFactory());
        ControllerLogin controlador = new ControllerLogin(manager, vista, modelo);
    }

}
