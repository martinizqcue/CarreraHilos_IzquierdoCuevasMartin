/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.carrerahilos_izquierdocuevasmartin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 *
 * @author alumno
 */

public class CarreraHilos extends javax.swing.JFrame {
  
    /**
     * Creates new form CarreraHilos
     */
    
    
    private JPanel panel;
    private JLabel etiquetaImagen;
    
    private JLabel[] coches; // Imágenes de los coches
    private JProgressBar[] barrasProgreso; // Barras de progreso para los coches
    private Thread[] hilosCoches; // Hilos para los coches
    private AtomicInteger[] posiciones; // Posiciones de los coches
    private AtomicBoolean carreraTerminada; // Flag para saber si la carrera ha terminado
    private int distancia = 1200; // Distancia del circuito
    private String[] colores = {"Rojo", "Verde", "Azul", "Rosa"}; // Colores de los coches

   // Nuevo JTextField para ingresar la distancia del circuito
    private JTextField inputDistancia;
    
    private JButton botonAplicar;
    
    public CarreraHilos() {
        initComponents();
        
    setTitle("Ejemplo de Fondo con ProgressBar Responsive");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1500, 600);
    setLocationRelativeTo(null);
    setResizable(false); // Cambié a true para que puedas redimensionar la ventana

    // Panel para contener los componentes visibles
    JPanel panelComponentes = new JPanel();
    panelComponentes.setLayout(null);
    panelComponentes.setOpaque(false); // Hacerlo transparente para que la imagen de fondo se vea

    // Agregar el JTextField para ingresar la distancia
    inputDistancia = new JTextField(String.valueOf(distancia)); // Inicializar con la distancia predeterminada
    panelComponentes.add(inputDistancia);

    // Botón para aplicar la nueva distancia
    botonAplicar = new JButton("Aplicar Distancia");
    botonAplicar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Intentar convertir el valor introducido en el JTextField a un entero
                int nuevaDistancia = Integer.parseInt(inputDistancia.getText());
                distancia = nuevaDistancia; // Actualizar la distancia del circuito

                // Calcular el nuevo ancho de la ventana basado en la distancia
                // Relación entre distancia y ancho (1.25 píxeles por metro)
                double relacion = 1.25;  // 1500 píxeles / 1200 metros
                int nuevoWidth = (int) (distancia * relacion);

                // Ajustar el tamaño de la ventana (nuevo width, mantener la altura actual)
                setSize(nuevoWidth, getHeight()); // Actualiza el tamaño de la ventana

                // Redimensionar la imagen de inicio
                int nuevaAnchuraImagen = (int) (nuevoWidth * 0.33);  // Ajustamos la imagen a un porcentaje del nuevo ancho
                int nuevaAlturaImagen = nuevaAnchuraImagen / 2; // Mantener la relación de aspecto
                etiquetaImagen.setBounds((nuevoWidth - nuevaAnchuraImagen) / 2, (getHeight() - nuevaAlturaImagen) / 2, nuevaAnchuraImagen, nuevaAlturaImagen);

                // Redimensionar el JTextField y el botón
                inputDistancia.setBounds(nuevoWidth - 400, getHeight() - 100, 100, 30);
                botonAplicar.setBounds(nuevoWidth - 280, getHeight() - 100, 150, 30);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Por favor, introduce un número válido para la distancia.");
            }
        }
    });
    panelComponentes.add(botonAplicar);

    // Crear el panel de fondo con imagen
    BackgroundPanel backgroundPanel = new BackgroundPanel("Resources/circuito.jpg");
    setContentPane(backgroundPanel);
    setLayout(null);

    // Agregar el panel con los componentes encima del fondo
    add(panelComponentes);
    panelComponentes.setBounds(0, 0, getWidth(), getHeight());

    // Agregar imágenes y elementos de la carrera
    agregarImagenInteractiva(backgroundPanel);
    agregarImagenes(backgroundPanel);

    // Ajustar componentes al cambiar el tamaño de la ventana
    backgroundPanel.setLayout(null);
    addComponentListener(new java.awt.event.ComponentAdapter() {
        @Override
        public void componentResized(java.awt.event.ComponentEvent evt) {
            ajustarComponentes();
        }
    });

    // Ajustar componentes al inicio
    ajustarComponentes();

    setVisible(true);
}
    
    // Método para agregar la imagen interactiva de correr
private void agregarImagenInteractiva(JPanel panel) {
    String ruta = "Resources/correr.png";
    etiquetaImagen = new JLabel();
    try {
        Image img = ImageIO.read(new File(ruta));
        img = img.getScaledInstance(500, 250, Image.SCALE_SMOOTH);
        etiquetaImagen.setIcon(new ImageIcon(img));
    } catch (IOException e) {
        System.out.println("No se pudo cargar la imagen: " + ruta);
    }

    int anchoEtiqueta = 500;
    int altoEtiqueta = 250;
    int xPos = (getWidth() - anchoEtiqueta) / 2;
    int yPos = (getHeight() - altoEtiqueta) / 2;
    etiquetaImagen.setBounds(xPos, yPos, anchoEtiqueta, altoEtiqueta);
    panel.add(etiquetaImagen);

    // Evento para iniciar la carrera
    etiquetaImagen.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (!carreraTerminada.get()) {
                iniciarCarrera(); // Iniciar la carrera si no ha terminado
            }
        }
    });
}


    // Método para agregar las imágenes de los coches
    private void agregarImagenes(JPanel panel) {
    String[] rutas = {
        "Resources/cocherojo.png",
        "Resources/cocheverde.png",
        "Resources/cocheazul.png",
        "Resources/cocherosa.png"
    };

    int yPos = 50; // Altura inicial para los coches y barras
    coches = new JLabel[4];
    barrasProgreso = new JProgressBar[4];
    hilosCoches = new Thread[4];
    posiciones = new AtomicInteger[4];
    carreraTerminada = new AtomicBoolean(false);

    // Colores de fondo para las barras de progreso
    java.awt.Color[] coloresFondo = {
        java.awt.Color.RED,
        java.awt.Color.GREEN,
        java.awt.Color.BLUE,
        java.awt.Color.PINK
    };

    for (int i = 0; i < rutas.length; i++) {
        try {
            // Crear etiquetas para los coches
            coches[i] = new JLabel();
            Image img = ImageIO.read(new File(rutas[i]));
            img = img.getScaledInstance(180, 80, Image.SCALE_SMOOTH);
            coches[i].setIcon(new ImageIcon(img));
            coches[i].setBounds(10, yPos + i * 120, 180, 80);
            panel.add(coches[i]);

            // Crear barras de progreso
            barrasProgreso[i] = new JProgressBar(0, 100);
            barrasProgreso[i].setValue(0);
            barrasProgreso[i].setStringPainted(true); // Mostrar el porcentaje
            barrasProgreso[i].setFont(new Font("Arial", Font.BOLD, 12)); // Asegurarse de que el texto sea legible

            // Establecer el color de la barra utilizando setUI para una personalización completa
            switch (i) {
                case 0: // Rojo
                    Color colorRojo = new java.awt.Color(255, 0, 0);
                    barrasProgreso[i].setUI(crearProgressBarUI(colorRojo, new java.awt.Color(255, 200, 200), oscurecerColor(colorRojo)));
                    break;
                case 1: // Verde
                    Color colorVerde = new java.awt.Color(0, 255, 0);
                    barrasProgreso[i].setUI(crearProgressBarUI(colorVerde, new java.awt.Color(200, 255, 200), oscurecerColor(colorVerde)));
                    break;
                case 2: // Azul
                    Color colorAzul = new java.awt.Color(0, 0, 255);
                    barrasProgreso[i].setUI(crearProgressBarUI(colorAzul, new java.awt.Color(200, 200, 255), oscurecerColor(colorAzul)));
                    break;
                case 3: // Rosa
                    Color colorRosa = new java.awt.Color(255, 0, 255);
                    barrasProgreso[i].setUI(crearProgressBarUI(colorRosa, new java.awt.Color(255, 200, 255), oscurecerColor(colorRosa)));
                    break;
            }

            // Posicionar las barras de progreso justo debajo de los coches
            barrasProgreso[i].setBounds(200, yPos + i * 120 + 100, getWidth() - 220, 20); // Ajustar ancho dinámico
            panel.add(barrasProgreso[i]);

            // Inicializar posiciones de progreso
            posiciones[i] = new AtomicInteger(0);
        } catch (IOException e) {
            System.out.println("No se pudo cargar la imagen: " + rutas[i]);
        }
    }

    // Actualizar el panel después de agregar los elementos
    panel.revalidate();
    panel.repaint();
}

// Método para crear un ProgressBarUI con colores personalizados
private ProgressBarUI crearProgressBarUI(final Color colorPrimerPlano, final Color colorFondo, final Color colorTexto) {
    return new BasicProgressBarUI() {
        @Override
        public void paintDeterminate(Graphics g, JComponent c) {
            super.paintDeterminate(g, c);
            JProgressBar barraProgreso = (JProgressBar) c;
            int anchoBarra = barraProgreso.getWidth();
            int alturaBarra = barraProgreso.getHeight();

            // Dibujar el fondo de la barra de progreso
            g.setColor(colorFondo);
            g.fillRect(0, 0, anchoBarra, alturaBarra);

            // Dibujar el progreso
            int anchoProgreso = (int) (anchoBarra * barraProgreso.getPercentComplete());
            g.setColor(colorPrimerPlano);
            g.fillRect(0, 0, anchoProgreso, alturaBarra);

            // Dibujar el texto del porcentaje directamente en la barra
            String textoPorcentaje = barraProgreso.getString();
            if (textoPorcentaje != null && !textoPorcentaje.isEmpty()) {
                g.setColor(colorTexto); // Usar el color oscuro del porcentaje
                g.setFont(new Font("Arial", Font.BOLD, 12)); // Fuente legible
                FontMetrics fm = g.getFontMetrics();
                int anchoTexto = fm.stringWidth(textoPorcentaje);
                int alturaTexto = fm.getAscent();

                // Centrar el texto
                g.drawString(textoPorcentaje, (anchoBarra - anchoTexto) / 2, (alturaBarra + alturaTexto) / 2 - 2);
            }
        }
    };
}

// Método para oscurecer el color original
private Color oscurecerColor(Color color) {
    int rojo = (int) (color.getRed() * 0.7);
    int verde = (int) (color.getGreen() * 0.7);
    int azul = (int) (color.getBlue() * 0.7);
    
    // Asegurarse de que no se pase de 0 para cada componente de color
    rojo = Math.max(rojo, 0);
    verde = Math.max(verde, 0);
    azul = Math.max(azul, 0);
    
    return new Color(rojo, verde, azul);
}

    // Método para iniciar la carrera
    private void iniciarCarrera() {
        etiquetaImagen.setVisible(false);

        for (int i = 0; i < 4; i++) {
            int indiceCoche = i;
            hilosCoches[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    int velocidad = (int) (Math.random() * 1000 + 1);
                    while (posiciones[indiceCoche].get() < distancia && !carreraTerminada.get()) {
                        try {
                            Thread.sleep(1000 / velocidad);
                            int avance = posiciones[indiceCoche].incrementAndGet();
                            coches[indiceCoche].setBounds(10 + avance, 50 + indiceCoche * 120, 180, 80);
                            barrasProgreso[indiceCoche].setValue(avance * 100 / distancia);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (posiciones[indiceCoche].get() >= distancia && !carreraTerminada.get()) {
                        carreraTerminada.set(true);
                        anunciarGanador(indiceCoche);
                    }
                }
            });
            hilosCoches[i].start();
        }
    }

    private void anunciarGanador(int cocheGanador) {
    String colorGanador = colores[cocheGanador];
    JOptionPane.showMessageDialog(this, "¡El coche " + (cocheGanador + 1) + " (" + colorGanador + ") ha ganado!");
    for (int i = 0; i < 4; i++) {
        if (i != cocheGanador) {
            JOptionPane.showMessageDialog(this, "El coche " + (i + 1) + " (" + colores[i] + ") recorrió " + posiciones[i].get() + " metros.");
        }
    }

    // Reiniciar la carrera
    reiniciarCarrera();
}
    
    private void reiniciarCarrera() {
    // Ocultar coches y barras de progreso
    for (int i = 0; i < 4; i++) {
        coches[i].setVisible(false);  // Ocultar los coches al reiniciar
        barrasProgreso[i].setValue(0);  // Reiniciar la barra de progreso
        barrasProgreso[i].setVisible(true);  // Asegurarnos de que la barra esté visible
    }

    // Restablecer las posiciones de los coches y hacerlos visibles
    for (int i = 0; i < 4; i++) {
        posiciones[i].set(0);  // Restablecer la posición del coche a 0
        // Establecer las posiciones iniciales de los coches
        coches[i].setBounds(10, 50 + i * 120, 180, 80);  // Volver a la posición inicial
        coches[i].setVisible(true);  // Hacer visibles los coches
    }

    // Volver a hacer visible la imagen interactiva para empezar de nuevo
    etiquetaImagen.setVisible(true);

    // Restablecer la variable de carrera terminada
    carreraTerminada.set(false);
}






    // Método para ajustar los componentes al cambiar el tamaño de la ventana
    private void ajustarComponentes() {
    int width = getWidth();
    int height = getHeight();

    // Ajustar el JTextField y el botón
    inputDistancia.setBounds(10, height - 100, 100, 30);
    botonAplicar.setBounds(120, height - 100, 150, 30);

    // Calcular el nuevo tamaño de la imagen
    int nuevaAnchuraImagen = (int) (width * 0.4);  // La imagen ocupa el 40% del ancho de la ventana
    int nuevaAlturaImagen = (int) (nuevaAnchuraImagen * 0.5); // Mantener la relación de aspecto 2:1

    // Redimensionar la imagen
    ImageIcon iconoOriginal = new ImageIcon("Resources/correr.png");
    Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(nuevaAnchuraImagen, nuevaAlturaImagen, Image.SCALE_SMOOTH);
    ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);

    // Actualizar el JLabel con la imagen escalada
    etiquetaImagen.setIcon(iconoEscalado);
    etiquetaImagen.setBounds(
        (width - nuevaAnchuraImagen) / 2, // Centrar horizontalmente
        (height - nuevaAlturaImagen) / 2, // Centrar verticalmente
        nuevaAnchuraImagen,
        nuevaAlturaImagen
    );

    // Ajustar las barras de progreso
    for (int i = 0; i < barrasProgreso.length; i++) {
        int barraAncho = (int) (width * 0.5); // Ocupa el 50% del ancho de la ventana
        int barraX = (width - barraAncho) / 2; // Centrar las barras horizontalmente
        int barraY = 50 + i * 120 + 100; // Posición vertical debajo de los coches ajustada
        barrasProgreso[i].setBounds(barraX, barraY, barraAncho, 20); // Aplicar las dimensiones calculadas
    }
}





    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("No se pudo cargar la imagen: " + imagePath);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                Image scaledImage = backgroundImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
                g.drawImage(scaledImage, 0, 0, this);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 729, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 483, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CarreraHilos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CarreraHilos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CarreraHilos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CarreraHilos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CarreraHilos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
