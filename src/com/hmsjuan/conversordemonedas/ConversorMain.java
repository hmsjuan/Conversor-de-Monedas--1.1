package com.hmsjuan.conversordemonedas;


import com.hmsjuan.conversordemonedas.herramientas.Conversor;
import com.hmsjuan.conversordemonedas.herramientas.Currency;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.DecimalFormat;

import java.text.SimpleDateFormat;
import java.util.*;

public class ConversorMain {
    private JComboBox cBoxOrigen,cBoxDestino;
    private JButton btnCambiar;
    private JLabel lblResultado;
    private JList lstResultados;
    private JPanel jPConversor;
    private JTextField textCantidad;
    private JLabel lblEstatus;
    private String iMonedaOrigen, iMonedaDestino;
    private double resultado, valoraConvertir;
    ArrayList<String> historico = new ArrayList<String>();


    public static void main(String[] args) throws IOException {
        JFrame framConversor = new JFrame("Conversor de Monedas y Temperatura");
        framConversor.setContentPane(new ConversorMain().jPConversor);
        framConversor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framConversor.pack();
        framConversor.setLocale(null);
        framConversor.setSize(650,450);
        framConversor.setResizable(false);
        framConversor.setVisible(true);



    }
    private void mostrarMensaje(JLabel label, String texto, Color color){
        lblEstatus.setText(texto);
        lblEstatus.setForeground(color);
        lblEstatus.setVisible(true);
    }
    public ConversorMain(){

        Conversor conversor = new Conversor("DOP");
        try {
            conversor.obtenerTasasDeCambio();
        } catch (IOException e) {
            mostrarMensaje(lblEstatus,"Error al obtener las tasas de cambio", Color.red );

            return;

        }
        mostrarMensaje(lblEstatus, "Tasas de cambio obtenidas exitosamente.", Color.BLUE);

        llenarComboBox(cBoxOrigen);
        llenarComboBox(cBoxDestino);

        cBoxOrigen.addActionListener(e -> iMonedaOrigen = Objects.requireNonNull(cBoxOrigen.getSelectedItem()).toString().substring(0,3));
        cBoxDestino.addActionListener(e -> iMonedaDestino = Objects.requireNonNull(cBoxDestino.getSelectedItem()).toString().substring(0,3));
        btnCambiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String   valorTemporal = textCantidad.getText();
              if (isNumeric(valorTemporal)){
                  valoraConvertir = Double.parseDouble(valorTemporal);
                  resultado = conversor.convertir(iMonedaOrigen, iMonedaDestino, valoraConvertir);
                  DecimalFormat df = new DecimalFormat("#.00");


                  String tResuldato = df.format(valoraConvertir)+" "+cBoxOrigen.getSelectedItem()+" equivalen a " +df.format(resultado)
                          +" "+cBoxDestino.getSelectedItem();
                  lblResultado.setText(tResuldato);
                  historico.add(fachaYHora()+" "+tResuldato);

                  Comparator<String> comparador = Collections.reverseOrder();
                  Collections.sort(historico, comparador);


                  lstResultados.setListData(historico.toArray());

              }else{
                  JOptionPane.showMessageDialog(null, "El valor ingresado no es válido");
              }


            }
        });

        textCantidad.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char caracter = e.getKeyChar();
                if ((caracter <'0' || caracter>'9')&&(caracter<',' || caracter>'.')) e.consume();
            }
        });
    }

    public void llenarComboBox(JComboBox comboBox){
//Monedas monedas = new Monedas();
        Currency currency = new Currency();
        currency.llenarMonedas();

       // monedas.llenarMonedas();
        for (Map.Entry entry: currency.listaMonedas.entrySet() ){
            Object items = entry.getKey();
            Object nombres = entry.getValue();

            String nombre= (String) items + "-"+ (String) nombres;
            comboBox.addItem((String) nombre);
        }
    }

    private static boolean isNumeric(String numero){
        //Verificacion de numeros ingresados
        return numero != null && numero.matches("[-+]?\\d*\\.?\\d+");
    }
    private String fachaYHora(){
        Date fechaActual = new Date();
        // Crear un objeto SimpleDateFormat para formatear la fecha y hora
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy 'a las' hh:mm a");
        // Formatear la fecha y hora actual según el formato deseado
        return formato.format(fechaActual);
    }

}
