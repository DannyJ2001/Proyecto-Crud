/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import View.ViewAdministrador;
import ViewInterna.ViewPersona;
import ViewInterna.ViewProducto;
import ViewInterna.ViewUsuario;
import static java.awt.Frame.MAXIMIZED_BOTH;
import modelo.PersonaJpaController;
import modelo.ProductoJpaController;
import modelo.UsuarioJpaController;
import proyecto_producto.ManageFactory;

/**
 *
 * @author danny
 */
public class ControllerAdministrador {

    ViewAdministrador vista;
    ManageFactory manage;

    public ControllerAdministrador(ViewAdministrador vista, ManageFactory manage) {
        this.vista = vista;
        this.manage = manage;
        
        this.vista.setExtendedState(MAXIMIZED_BOTH);
        controlEvento();
    }

    public void controlEvento() {

        vista.getjMenuPersona().addActionListener(l -> cargarVistaPersona());
        vista.getjMenuUsuario().addActionListener(l -> cargarVistaUsuario());
        vista.getjMenuProducto().addActionListener(l -> cargarVistaProducto());

    }

    public static ViewPersona vp;

    public void cargarVistaPersona() {

        new ControllerPersona(vp, manage, new PersonaJpaController(manage.getEntityManagerFactory()), this.vista.getEscritorio());

    }
    
    
    public static ViewUsuario vu;

    public void cargarVistaUsuario() {
        
        new ControllerUsuario(vu, manage, new UsuarioJpaController(manage.getEntityManagerFactory()), this.vista.getEscritorio());
         

    }
    
    public static ViewProducto vpr;

    public void cargarVistaProducto(){
        
        new ControllerProducto(vpr, manage, new ProductoJpaController(manage.getEntityManagerFactory()), this.vista.getEscritorio());
         

    }
    

}
