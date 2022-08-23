/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import ViewInterna.ViewUsuario;
import java.awt.Dimension;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import modelo.Persona;
import modelo.PersonaJpaController;
import modelo.Usuario;
import modelo.UsuarioJpaController;
import modelo.exceptions.NonexistentEntityException;
import proyecto_producto.ManageFactory;

/**
 *
 * @author danny
 */
public class ControllerUsuario {

    ViewUsuario vista;
    ManageFactory manage;
    UsuarioJpaController modeloUsuario;
    Usuario usuario;
    JDesktopPane panelEscritorio;
    ModeloTablaUsuario modeloTabla;
    ListSelectionModel listaUsuarioModel;

    public ControllerUsuario(ViewUsuario vista, ManageFactory manage, UsuarioJpaController modeloUsuario, JDesktopPane panelEscritorio) {
        
        this.manage = manage;
        this.modeloUsuario = modeloUsuario;
        this.panelEscritorio = panelEscritorio;
        this.modeloTabla = new ModeloTablaUsuario();
        this.modeloTabla.setFilas(this.modeloUsuario.findUsuarioEntities());
        
        if (ControllerAdministrador.vu == null) {
            ControllerAdministrador.vu = new ViewUsuario();
            this.vista = ControllerAdministrador.vu;
            this.panelEscritorio.add(this.vista);
            
            this.vista.getTablaUsuario().setModel(modeloTabla);
            ControllerAdministrador.vu.show();
            
            iniciar();
            cargarCombos();
            
            //Para centar la vista en la ventana
            Dimension desktopSize = this.panelEscritorio.getSize();
            Dimension FrameSize = this.vista.getSize();
            this.vista.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
            
        }else{
            ControllerAdministrador.vu.show();
        }
        
    }

    public void iniciar() {
        this.vista.getBtnCrear().addActionListener(l -> crearUsuario());
        this.vista.getBtnEditar().addActionListener(l -> editarUsuario());
        this.vista.getBtnEliminar().addActionListener(l -> eliminar());
        this.vista.getBtnLimpiar().addActionListener(l -> limpiar());
        this.vista.getBtnBuscar().addActionListener(l -> buscarUsuario());
        this.vista.getCheckMostarTodo().addActionListener(l -> limpiarBuscador());
        this.vista.getBtnLimpiarBuscar().addActionListener(l -> limpiarBuscador());
        
        this.vista.getTablaUsuario().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaUsuarioModel = this.vista.getTablaUsuario().getSelectionModel();
        listaUsuarioModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    usuarioSeleccionado();
                }
            }
        });

        this.vista.getBtnEliminar().setEnabled(false);
        this.vista.getBtnEditar().setEnabled(false);

    }

    public void crearUsuario() {
        Usuario usuario = new Usuario();
     
        usuario.setUsuario(this.vista.getTxtUsuario().getText());
        usuario.setClave(this.vista.getTxtClave().getText());
        usuario.setIdpersona((Persona)this.vista.getComboPersona().getSelectedItem());

        modeloUsuario.create(usuario);
        modeloTabla.agregar(usuario);
        JOptionPane.showMessageDialog(panelEscritorio, "Usuario creado Correctamente");
        limpiar();
    }

    public void editarUsuario() {
        if (usuario != null) {

            usuario.setUsuario(this.vista.getTxtUsuario().getText());
            usuario.setClave(this.vista.getTxtClave().getText());
            usuario.setIdpersona((Persona)this.vista.getComboPersona().getSelectedItem());

            try {
                modeloUsuario.edit(usuario);
                modeloTabla.eliminar(usuario);
                modeloTabla.actualizar(usuario);
                limpiar();
            } catch (Exception ex) {
                Logger.getLogger(ControllerPersona.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public void eliminar() {
        if (usuario != null) {

            try {
                modeloUsuario.destroy(usuario.getIdusuario());
                limpiar();
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(ControllerPersona.class.getName()).log(Level.SEVERE, null, ex);
            }
            modeloTabla.eliminar(usuario);
            JOptionPane.showMessageDialog(panelEscritorio, "Usuario eliminado correctamente");
        }
    }

    public void limpiar() {
        
        vista.getTxtUsuario().setText("");
        vista.getTxtClave().setText("");
        vista.getComboPersona().setSelectedItem(0);


        this.vista.getBtnEliminar().setEnabled(false);
        this.vista.getBtnEditar().setEnabled(false);
        this.vista.getBtnCrear().setEnabled(true);

        this.vista.getTablaUsuario().getSelectionModel().clearSelection();
    }

    public void usuarioSeleccionado() {
        if (this.vista.getTablaUsuario().getSelectedRow() != -1) {
            usuario = modeloTabla.getFilas().get(this.vista.getTablaUsuario().getSelectedRow());
 
           
            this.vista.getTxtUsuario().setText(usuario.getUsuario());
            this.vista.getTxtClave().setText(usuario.getClave());
            this.vista.getComboPersona().setSelectedItem(usuario.getIdpersona());


            this.vista.getBtnEliminar().setEnabled(true);
            this.vista.getBtnEditar().setEnabled(true);
            this.vista.getBtnCrear().setEnabled(false);
            

        }
    }

    public void buscarUsuario() {
       
        if (this.vista.getCheckMostarTodo().isSelected()) {
            modeloTabla.setFilas(modeloUsuario.findUsuarioEntities());
            modeloTabla.fireTableDataChanged();
           
            
        }

        if (!this.vista.getTxtBuscador().getText().equals("")) {
            modeloTabla.setFilas(modeloUsuario.buscarUsuario1(this.vista.getTxtBuscador().getText()));
            modeloTabla.fireTableDataChanged();
        } else {
            limpiarBuscador();
        }
    }

    public void limpiarBuscador() {
        this.vista.getTxtBuscador().setText("");
        modeloTabla.setFilas(modeloUsuario.findUsuarioEntities());
        modeloTabla.fireTableDataChanged();
    }

    
    public void cargarCombos(){
        try{
        Vector v = new Vector();
        v.addAll(new PersonaJpaController(manage.getEntityManagerFactory()).findPersonaEntities());
        this.vista.getComboPersona().setModel(new DefaultComboBoxModel(v));
        }catch (ArrayIndexOutOfBoundsException ex){
            System.out.println("Error");
        }
        
    }

    
    
    
}
