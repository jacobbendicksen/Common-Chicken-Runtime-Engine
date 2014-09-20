package ccre.igneous;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

/**
 * A panel representing a single joystick.
 *
 * @author skeggsc
 */
@SuppressWarnings("serial")
public class EmulatorJoystick extends javax.swing.JPanel {

    /**
     * Creates new form EmulatorJoystick
     */
    public EmulatorJoystick() {
        initComponents();
        joy = new EmuJoystick(new JToggleButton[]{btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12}, new JSlider[]{axis1, axis2, axis3, axis4, axis5, axis6});
    }
    /**
     * The EmuJoystick connected to this GUI joystick.
     */
    public final EmuJoystick joy;

    /**
     * Set the name of the Joystick.
     *
     * @param name the new name of the Joystick.
     */
    public void setText(String name) {
        this.labTitle.setText(name);
    }

    /**
     * @return the name of the Joystick.
     */
    public String getText() {
        return this.labTitle.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labTitle = new javax.swing.JLabel();
        axis1 = new javax.swing.JSlider();
        axis2 = new javax.swing.JSlider();
        axis3 = new javax.swing.JSlider();
        axis4 = new javax.swing.JSlider();
        axis5 = new javax.swing.JSlider();
        axis6 = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        btn1 = new javax.swing.JToggleButton();
        btn4 = new javax.swing.JToggleButton();
        btn7 = new javax.swing.JToggleButton();
        btn10 = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        btn2 = new javax.swing.JToggleButton();
        btn5 = new javax.swing.JToggleButton();
        btn8 = new javax.swing.JToggleButton();
        btn11 = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        btn3 = new javax.swing.JToggleButton();
        btn6 = new javax.swing.JToggleButton();
        btn9 = new javax.swing.JToggleButton();
        btn12 = new javax.swing.JToggleButton();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        labTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labTitle.setText("Joystick ");

        axis1.setMaximum(10);
        axis1.setMinimum(-10);
        axis1.setMinorTickSpacing(1);
        axis1.setSnapToTicks(true);
        axis1.setValue(0);

        axis2.setMaximum(10);
        axis2.setMinimum(-10);
        axis2.setMinorTickSpacing(1);
        axis2.setSnapToTicks(true);
        axis2.setValue(0);

        axis3.setMaximum(10);
        axis3.setMinimum(-10);
        axis3.setMinorTickSpacing(1);
        axis3.setSnapToTicks(true);
        axis3.setValue(0);

        axis4.setMaximum(10);
        axis4.setMinimum(-10);
        axis4.setMinorTickSpacing(1);
        axis4.setSnapToTicks(true);
        axis4.setValue(0);

        axis5.setMaximum(10);
        axis5.setMinimum(-10);
        axis5.setMinorTickSpacing(1);
        axis5.setSnapToTicks(true);
        axis5.setValue(0);

        axis6.setMaximum(10);
        axis6.setMinimum(-10);
        axis6.setMinorTickSpacing(1);
        axis6.setSnapToTicks(true);
        axis6.setValue(0);

        btn1.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn1.setText("1");
        btn1.setToolTipText("");
        btn1.setMargin(new java.awt.Insets(2, 4, 2, 4));

        btn4.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn4.setText("4");
        btn4.setMargin(new java.awt.Insets(2, 4, 2, 4));

        btn7.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn7.setText("7");
        btn7.setToolTipText("");
        btn7.setMargin(new java.awt.Insets(2, 4, 2, 4));

        btn10.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn10.setText("10");
        btn10.setMargin(new java.awt.Insets(2, 4, 2, 4));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btn1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btn1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn10))
        );

        btn2.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn2.setText("2");
        btn2.setMargin(new java.awt.Insets(2, 4, 2, 4));

        btn5.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn5.setText("5");
        btn5.setToolTipText("");
        btn5.setMargin(new java.awt.Insets(2, 4, 2, 4));

        btn8.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn8.setText("8");
        btn8.setMargin(new java.awt.Insets(2, 4, 2, 4));

        btn11.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn11.setText("11");
        btn11.setMargin(new java.awt.Insets(2, 4, 2, 4));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btn2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btn2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn11))
        );

        btn3.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn3.setText("3");
        btn3.setMargin(new java.awt.Insets(2, 4, 2, 4));

        btn6.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn6.setText("6");
        btn6.setMargin(new java.awt.Insets(2, 4, 2, 4));

        btn9.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn9.setText("9");
        btn9.setMargin(new java.awt.Insets(2, 4, 2, 4));

        btn12.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        btn12.setText("12");
        btn12.setMargin(new java.awt.Insets(2, 4, 2, 4));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btn9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(btn3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(axis1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(axis2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(axis3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(axis4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(axis5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(axis6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(labTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(labTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(axis1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(axis2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(axis3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(axis4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(axis5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(axis6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider axis1;
    private javax.swing.JSlider axis2;
    private javax.swing.JSlider axis3;
    private javax.swing.JSlider axis4;
    private javax.swing.JSlider axis5;
    private javax.swing.JSlider axis6;
    private javax.swing.JToggleButton btn1;
    private javax.swing.JToggleButton btn10;
    private javax.swing.JToggleButton btn11;
    private javax.swing.JToggleButton btn12;
    private javax.swing.JToggleButton btn2;
    private javax.swing.JToggleButton btn3;
    private javax.swing.JToggleButton btn4;
    private javax.swing.JToggleButton btn5;
    private javax.swing.JToggleButton btn6;
    private javax.swing.JToggleButton btn7;
    private javax.swing.JToggleButton btn8;
    private javax.swing.JToggleButton btn9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel labTitle;
    // End of variables declaration//GEN-END:variables
}
