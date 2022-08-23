/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import ViewInterna.ViewPersona;
import java.awt.Dimension;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import modelo.Persona;
import modelo.PersonaJpaController;
import modelo.exceptions.NonexistentEntityException;
import proyecto_producto.ManageFactory;

/**
 *
 * @author danny
 */
public class ControllerPersona {

    ViewPersona vista;
    ManageFactory manage;
    PersonaJpaController modeloPersona;
    Persona persona;
    JDesktopPane panelEscritorio;
    ModeloTablaPersona modeloTabla;
    ListSelectionModel listaPersonaModel;

    public ControllerPersona(ViewPersona vista, ManageFactory manage, PersonaJpaController modeloPersona, JDesktopPane panelEscritorio) {

        this.manage = manage;
        this.modeloPersona = modeloPersona;
        this.panelEscritorio = panelEscritorio;
        this.modeloTabla = new ModeloTablaPersona();
        this.modeloTabla.setFilas(modeloPersona.findPersonaEntities());

        if (ControllerAdministrador.vp == null) {
            ControllerAdministrador.vp = new ViewPersona();
            this.vista = ControllerAdministrador.vp;
            this.panelEscritorio.add(this.vista);

            this.vista.getTablaPersona().setModel(modeloTabla);

            ControllerAdministrador.vp.show();
            iniciar();

            //Para centar la vista en la ventana
            Dimension desktopSize = this.panelEscritorio.getSize();
            Dimension FrameSize = this.vista.getSize();
            this.vista.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);

        } else {
            ControllerAdministrador.vp.show();
//            this.vista.show();
        }

    }

    public void iniciar() {
        this.vista.getBtnCrear().addActionListener(l -> crearPersona());
        this.vista.getBtnEditar().addActionListener(l -> editarPersona());
        this.vista.getBtnEliminar().addActionListener(l -> eliminar());
        this.vista.getBtnLimpiar().addActionListener(l -> limpiar());
        this.vista.getBtnBuscar().addActionListener(l -> buscarPersona());
        this.vista.getBtnLimpiarBuscar().addActionListener(l -> limpiarBuscador());
        this.vista.getCheckMostrarTodos().addActionListener(l -> limpiarBuscador());
        
        this.vista.getTablaPersona().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaPersonaModel = this.vista.getTablaPersona().getSelectionModel();
        listaPersonaModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    personaSeleccionada();
                }
            }
        });

        this.vista.getBtnEliminar().setEnabled(false);
        this.vista.getBtnEditar().setEnabled(false);

    }

    public void crearPersona() {
        Persona persona = new Persona();
        persona.setNombre(this.vista.getTxtNombre().getText());
        persona.setApellido(this.vista.getTxtApellido().getText());
        persona.setCedula(this.vista.getTxtCedula().getText());
        persona.setCelular(this.vista.getTxtCelular().getText());
        persona.setCorreo(this.vista.getTxtCorreo().getText());
        persona.setDireccion(this.vista.getTxtDireccion().getText());
        
        
        

        modeloPersona.create(persona);
        modeloTabla.agregar(persona);
        JOptionPane.showMessageDialog(panelEscritorio, "Persona creada Correctamente");
        limpiar();
    }

    public void editarPersona() {
        if (persona != null) {
            persona.setNombre(this.vista.getTxtNombre().getText());
            persona.setApellido(this.vista.getTxtApellido().getText());
            persona.setCedula(this.vista.getTxtCedula().getText());
            persona.setCelular(this.vista.getTxtCelular().getText());
            persona.setCorreo(this.vista.getTxtCorreo().getText());
            persona.setDireccion(this.vista.getTxtDireccion().getText());
            try {
                modeloPersona.edit(persona);
                modeloTabla.eliminar(persona);
                modeloTabla.actualizar(persona);
                limpiar();
            } catch (Exception ex) {
                Logger.getLogger(ControllerPersona.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public void eliminar() {
        if (persona != null) {

            try {
                modeloPersona.destroy(persona.getIdPersona());
                limpiar();
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(ControllerPersona.class.getName()).log(Level.SEVERE, null, ex);
            }
            modeloTabla.eliminar(persona);
            JOptionPane.showMessageDialog(panelEscritorio, "Persona eliminada correctamente");
        }
    }
    

    
    public void limpiar() {
        vista.getTxtNombre().setText("");
        vista.getTxtApellido().setText("");
        vista.getTxtCedula().setText("");
        vista.getTxtCelular().setText("");
        vista.getTxtCorreo().setText("");
        vista.getTxtDireccion().setText("");

        this.vista.getBtnEliminar().setEnabled(false);
        this.vista.getBtnEditar().setEnabled(false);
        this.vista.getBtnCrear().setEnabled(true);

        this.vista.getTablaPersona().getSelectionModel().clearSelection();
    }

    public void personaSeleccionada() {
        if (this.vista.getTablaPersona().getSelectedRow() != -1) {
            persona = modeloTabla.getFilas().get(this.vista.getTablaPersona().getSelectedRow());
            this.vista.getTxtNombre().setText(persona.getNombre());
            this.vista.getTxtApellido().setText(persona.getApellido());
            this.vista.getTxtCedula().setText(persona.getCedula());
            this.vista.getTxtCelular().setText(persona.getCelular());
            this.vista.getTxtDireccion().setText(persona.getDireccion());
            this.vista.getTxtCorreo().setText(persona.getCorreo());

            this.vista.getBtnEliminar().setEnabled(true);
            this.vista.getBtnEditar().setEnabled(true);
            this.vista.getBtnCrear().setEnabled(false);
            

        }
    }

    public void buscarPersona() {
       
        if (this.vista.getCheckMostrarTodos().isSelected()) {
            modeloTabla.setFilas(modeloPersona.findPersonaEntities());
            modeloTabla.fireTableDataChanged();
           
            
        }

        if (!this.vista.getTxtbuscador().getText().equals("")) {
            modeloTabla.setFilas(modeloPersona.buscarPersona(this.vista.getTxtbuscador().getText()));
            modeloTabla.fireTableDataChanged();
        } else {
            limpiarBuscador();
        }
    }

    public void limpiarBuscador() {
        this.vista.getTxtbuscador().setText("");
        modeloTabla.setFilas(modeloPersona.findPersonaEntities());
        modeloTabla.fireTableDataChanged();
    }

    
    

    
}
