/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import ViewInterna.ViewProducto;
import java.awt.Dimension;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import modelo.Persona;
import modelo.Producto;
import modelo.ProductoJpaController;
import modelo.exceptions.NonexistentEntityException;
import proyecto_producto.ManageFactory;

/**
 *
 * @author danny
 */
public class ControllerProducto {

    ViewProducto vista;
    ManageFactory manage;
    ProductoJpaController modeloProducto;
    Producto producto;
    JDesktopPane panelEscritorio;
    ModeloTablaProducto modeloTabla;
    ListSelectionModel listaProductoModel;

    public ControllerProducto(ViewProducto vista, ManageFactory manage, ProductoJpaController modeloProducto, JDesktopPane panelEscritorio) {

        this.manage = manage;
        this.modeloProducto = modeloProducto;
        this.panelEscritorio = panelEscritorio;
        this.modeloTabla = new ModeloTablaProducto();
        this.modeloTabla.setFilas(this.modeloProducto.findProductoEntities());

        if (ControllerAdministrador.vpr == null) {
            ControllerAdministrador.vpr = new ViewProducto();
            this.vista = ControllerAdministrador.vpr;
            this.panelEscritorio.add(this.vista);

            this.vista.getTablaProducto().setModel(modeloTabla);
            ControllerAdministrador.vpr.show();
            iniciar();

            //Para centar la vista en la ventana
            Dimension desktopSize = this.panelEscritorio.getSize();
            Dimension FrameSize = this.vista.getSize();
            this.vista.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);

        } else {
            ControllerAdministrador.vp.show();
        }
    }

    public void iniciar() {
        this.vista.getBtnCrear().addActionListener(l -> crearProducto());
        this.vista.getBtnEditar().addActionListener(l -> editarProducto());
        this.vista.getBtnEliminar().addActionListener(l -> eliminar());
        this.vista.getBtnLimpiar().addActionListener(l -> limpiar());
        this.vista.getBtnBuscar().addActionListener(l -> buscarProducto());
        this.vista.getBtnLimpiarBuscar().addActionListener(l -> limpiarBuscador());
        this.vista.getCheckMostrarTodos().addActionListener(l -> limpiarBuscador());

        this.vista.getTablaProducto().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaProductoModel = this.vista.getTablaProducto().getSelectionModel();
        listaProductoModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    productoSeleccionado();
                }
            }
        });

        this.vista.getBtnEliminar().setEnabled(false);
        this.vista.getBtnEditar().setEnabled(false);

    }

    public void crearProducto() {
        Producto producto = new Producto();

        producto.setNombre(this.vista.getTxtNombre().getText());
        producto.setCantidad(Integer.parseInt(this.vista.getTxtCantidad().getText()));
        producto.setPrecio(Double.parseDouble(this.vista.getTxtPrecio().getText()));

//        Integer nnn = Integer.valueOf(this.vista.getTxtPrecio().getText());
//        producto.setPrecio(BigInteger.valueOf(nnn.intValue()));

        modeloProducto.create(producto);
        modeloTabla.agregar(producto);
        
        Resources.success("Alerta", "Correcto");
       
        limpiar();
    }

    public void editarProducto() {
        if (producto != null) {
            producto.setNombre(this.vista.getTxtNombre().getText());
            producto.setCantidad(Integer.parseInt(this.vista.getTxtCantidad().getText()));

            try {
                modeloProducto.edit(producto);
                modeloTabla.eliminar(producto);
                modeloTabla.actualizar(producto);
                
                Resources.success("Alerta", "Editado correctamente");
                limpiar();
            } catch (Exception ex) {
                Logger.getLogger(ControllerPersona.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }


    
    public void eliminar() {
        if (producto != null) {

            try {
                modeloProducto.destroy(producto.getIdproducto());
                limpiar();
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(ControllerPersona.class.getName()).log(Level.SEVERE, null, ex);
            }
            modeloTabla.eliminar(producto);
             Resources.success("Alerta", "Eliminado correctamente");
        }
    }


    public void limpiar() {
        vista.getTxtNombre().setText("");
        vista.getTxtPrecio().setText("");
        vista.getTxtCantidad().setText("");

        this.vista.getBtnEliminar().setEnabled(false);
        this.vista.getBtnEditar().setEnabled(false);
        this.vista.getBtnCrear().setEnabled(true);

        this.vista.getTablaProducto().getSelectionModel().clearSelection();
        

        
    }

    public void productoSeleccionado() {
        if (this.vista.getTablaProducto().getSelectedRow() != -1) {
            producto = modeloTabla.getFilas().get(this.vista.getTablaProducto().getSelectedRow());
            
            this.vista.getTxtNombre().setText(producto.getNombre());
            this.vista.getTxtCantidad().setText(producto.getCantidad().toString());
            this.vista.getTxtPrecio().setText(producto.getPrecio().toString());
            
   
    
            //producto.setCantidad(Integer.parseInt(this.vista.getTxtCantidad().getText()));
            

//       Integer nnn = Integer.valueOf(this.vista.getTxtPrecio().getText());
//           

            this.vista.getBtnEliminar().setEnabled(true);
            this.vista.getBtnEditar().setEnabled(true);
            this.vista.getBtnCrear().setEnabled(false);

        }
    }

    public void buscarProducto() {

        if (this.vista.getCheckMostrarTodos().isSelected()) {
            modeloTabla.setFilas(modeloProducto.findProductoEntities());
            modeloTabla.fireTableDataChanged();

        }

        if (!this.vista.getTxtBuscador().getText().equals("")) {
            
            modeloTabla.setFilas(modeloProducto.buscarProducto(this.vista.getTxtBuscador().getText()));
            modeloTabla.fireTableDataChanged();
        } else {
            limpiarBuscador();
        }
    }

    public void limpiarBuscador() {
        this.vista.getTxtBuscador().setText("");
        modeloTabla.setFilas(modeloProducto.findProductoEntities());
        modeloTabla.fireTableDataChanged();
    }
}
