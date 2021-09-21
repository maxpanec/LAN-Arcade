/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author Maxwell
 */
public class Account extends javax.swing.JPanel {

    private Client client;
    public Account(Client client) {
        initComponents();
        this.client=client;
        jLabel2.setText("Username: " + client.name);
        jTextField1.setText(client.name);
        jTextField1.setVisible(false);
        jTextField1.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e){
                if(jTextField1.getText().length()>=12)
                    e.consume();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(153, 153, 153));
        setMinimumSize(new java.awt.Dimension(500, 500));
        setPreferredSize(new java.awt.Dimension(500, 500));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Account");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(218, 28, -1, -1));

        jLabel2.setText("Username:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 138, -1, -1));

        jLabel3.setForeground(new java.awt.Color(0, 51, 255));
        jLabel3.setText("Edit");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 181, -1, -1));

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(151, 178, 56, -1));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Back");
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        if(!jTextField1.isVisible())
            jTextField1.setVisible(true);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        String temp=evt.getActionCommand();
        if(client.notAllowedNames.contains(temp))
            return;
        client.name=temp;
        client.data.name=temp;
        jLabel2.setText("Username: " + client.name);
        System.out.println(temp);
        jTextField1.setVisible(false);
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        client.components.put(Menu.class, new Menu(client));
        client.frame.remove(client.components.get(Account.class));
        client.components.remove(Account.class);
        client.frame.add(client.components.get(Menu.class));
        client.frame.pack();
    }//GEN-LAST:event_jLabel4MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}