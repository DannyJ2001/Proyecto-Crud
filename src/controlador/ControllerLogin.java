/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import View.ViewAdministrador;
import View.View_Login;
import View.ViewAdministrador;
import javax.persistence.PersistenceException;
import javax.swing.JOptionPane;
import modelo.Usuario;
import modelo.UsuarioJpaController;
import proyecto_producto.ManageFactory;

/**
 *
 * @author danny
 */
public class ControllerLogin {

    private ManageFactory manager;
    private View_Login vista;
    private UsuarioJpaController modelo;
    
    ViewAdministrador admin = new ViewAdministrador();

    public ControllerLogin(ManageFactory manager, View_Login vista, UsuarioJpaController modelo) {
        this.manager = manager;
        this.vista = vista;
        this.modelo = modelo;
        this.vista.setLocationRelativeTo(null);
        this.vista.setVisible(true);
        iniciarControl();

    }

    public void iniciarControl() {

        vista.getBtnEntrar().addActionListener(l -> controlLogin());

        vista.getBtnCerrar().addActionListener(l -> cerrar());

    }

    public void cerrar() {
        System.exit(0);
    }

    public void controlLogin() {

        String usuario = vista.getTxtUsuario().getText();
        String clave = new String(vista.getjPassword().getPassword());
        
        try {
            Usuario user = modelo.buscarUsuario(usuario, clave);

        if (user != null) {
            
            JOptionPane.showMessageDialog(vista, "Usuario Correcto" );
            

            admin.setVisible(true);
            
            new ControllerAdministrador(admin, manager);
            
            vista.setVisible(false);

        } else {
            JOptionPane.showMessageDialog(vista, "Usuario Incorrecto");
 
            
        }
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(vista, "No existe conexion con la base");
        }
        

    }
}
