/*
 * أعوذ بالله من الشيطان الرجيم
 * بسم الله الرحمن الرحيم
 * Created by: Zubeida C. Khan zkhan@csir.co.za / zubzzz@hotmail.com
 Updated: 14 03 14: changed ontology URI
 Updated: 17 03 14: transferred annotations from source to target 
 (ontology, class, op, dp, annotationprop, and individual annotations), 
 created new SUGOI! annotations for target ontology
 Updated: 18 03 14: Implemented class subsumption mappings, 
 object property subsumption method, code optimisation
 by saving the final ontology twice only
 Updated: 19 03 14: Created two new methods  changeObjectPropertyClassDependencies() 
 and changeObjectPropertyObjectPropertyDependencies() to change the:
 domain, range, equivop, disjop, invop of the OP to the mapped ones
 Performed class and object property cleanup (on the fly taxonomy reordering)
 Updated 20 03 14: cleaning OP bugs, created a log txt file for the user, added ALL mapping files for user,
 added reset button
 Updated 27 03 14: added setProxy method but commented it out for upload- only to
 use for my testing. 
 Added new checkbox and emailing files to dropbox
 Updated 12 April 14: added new methods, deleteExtra() to delete extra mappable source ontology entities, and
 changeDpendantAxioms() to change equiv and subclassOf axioms of target entities tha reference mappable source entities
 */

package Interchangeability;

import javax.swing.*;
import java.util.Set;
import java.util.*;
import java.io.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.coode.owlapi.functionalparser.OWLFunctionalSyntaxParser;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.util.OWLOntologyURIChanger;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import java.text.DateFormat;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

class MyCustomFilter extends javax.swing.filechooser.FileFilter {
    @Override
    public boolean accept(File file) {
        // Allow only directories, or files with ".txt" extension
        return file.isDirectory() || file.getAbsolutePath().endsWith(".OWL") || file.getAbsolutePath().endsWith(".owl");
    }

    @Override
    public String getDescription() {
        // This description will be displayed in the dialog,
        // hard-coded = ugly, should be done via I18N
        return "OWL ontologies(*.owl)";
    }
}

/*
 * @author ZKhan
 */
public class Sugoi extends javax.swing.JApplet {
    
    private int count = 0;
    private int returnVal;
    private OWLOntologyManager managerDomainSource;
    private OWLOntologyManager managerFoundationalTarget;
    private OWLOntologyManager managerDomainTarget;
    private OWLOntologyManager managerFoundationalSource;
    private OWLOntologyManager managerMapper;
    private File domainSourceFile;
    private OWLOntology domainSourceOntology;
    private OWLOntology mapOntology;
    private OWLOntology targetFoundationalOntology;
    private OWLOntology domainTargetOntology;
    private OWLOntology sourceFoundationalOntology;
    private String oldText = "";
    private String newText = "";
    private String inputText = "";
    private String loadText = "";
    private String errorText = "";
    private boolean sourceFileBool = false;
    private boolean mapFileBool = false;
    private Set<OWLClass> mapArraySet = new HashSet();
    private ArrayList mapArray = new ArrayList();
    private Set<OWLObjectProperty> mapArrayOpSet = new HashSet();
    private ArrayList mapArrayOp = new ArrayList();
    private OWLDataFactory factory;
    private OWLDataFactory factorySourceBenchmark;
    private IRI targetIRI;    
    private IRI sourceIRI;
    private JLabel input = new JLabel();
    private int counto = 0;
    private String jTextPane2Text = "";
    private File file; //file for final onto
    private String fileName = ""; //filename for final onto
    private String tempf = "";
    private OWLClass tempClass;
    private Set<OWLClass> tempClassSet;
    private OWLObjectProperty tempOp;
    private Set<OWLObjectProperty> tempOpSet;
    private boolean interchange = false;
    private boolean ff = false;
    private OWLClass another;
    private int countOne = 0;
    private int countTwo = 0;
    private ArrayList<OWLClass> mapClasses = new ArrayList();
     private ArrayList<OWLObjectProperty> mapProperties = new ArrayList();
    private Set<OWLClassExpression> tempSet;
    private Set<OWLObjectPropertyExpression> tempSet2;
    private Set<OWLObjectPropertyExpression> tempSet3;
    private Set<OWLObjectPropertyExpression> tempSet4;
    private Set<OWLClass> strangeSet;
    private Set<OWLObjectProperty> strangeSet2;
    private boolean subBool = false;
    private boolean ancestorBool = false;    
    private int first = 0;
    private boolean yoho = false;
    private int hops = 0;
    private OWLClass target;
    private OWLObjectProperty target2;
    private OWLClass topclass;
    private OWLObjectProperty topop;
    private Set<OWLClassExpression> al;
    private Set<OWLObjectPropertyExpression> al2;
    private Date date = new Date();
    private DateFormat time= DateFormat.getTimeInstance();
    private OWLOntologyURIChanger change;    
    private String sourceFO;
    private String targetFO;
    private File logfile;    
    private ArrayList <OWLAxiom> axiomList = new ArrayList();
    private int badaxiomcount=0;
    private int goodaxiomcount=0;
    private ArrayList <OWLAxiom> goodaxiomList = new ArrayList();
    private ArrayList <OWLAxiom> badaxiomList = new ArrayList();
    private ArrayList <OWLClass> classList = new ArrayList();
    private ArrayList <OWLObjectProperty>  opList = new ArrayList();
    private ArrayList <OWLDataProperty>  dpList = new ArrayList();
    private ArrayList <OWLIndividual>  inList = new ArrayList();
    private ArrayList <OWLClass> sourceclassList = new ArrayList();
    private ArrayList <OWLObjectProperty>  sourceopList = new ArrayList();
    private ArrayList <OWLDataProperty>  sourcedpList = new ArrayList();
    private ArrayList <OWLIndividual>  sourceinList = new ArrayList();

  
       //!! please remember to reinitialise new variables in reset method!!
    
    /**
     * Initializes the applet Sugoi
     */
    @Override
    public void init() {
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
            java.util.logging.Logger.getLogger(Sugoi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Sugoi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Sugoi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Sugoi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the applet */
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
                    new Sugoi().setVisible(true);                    
                   jProgressBar1.setStringPainted(true);
                   jCheckBox1.setSelected(true);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

        fileChooser.setFileFilter(new MyCustomFilter());
        fileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileChooserActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));
        jPanel4.setPreferredSize(new java.awt.Dimension(600, 609));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setBackground(new java.awt.Color(204, 255, 204));
        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("SUGOI! Log");

        jProgressBar1.setBackground(new java.awt.Color(255, 51, 255));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("Output");

        jScrollPane2.setViewportView(jTextPane2);

        jLabel6.setBackground(new java.awt.Color(204, 255, 204));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("SUGOI! Progress");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setPreferredSize(new java.awt.Dimension(750, 257));

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setText("Load domain ontology");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("Interchange");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jList1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "1. DOLCE to BFO", "    BFO to DOLCE", "2. BFO to GFO", "    GFO to BFO", "3. GFO to DOLCE", "    DOLCE to GFO", "4. DOLCE to BFORO", "    BFORO to DOLCE", "5. BFORO to GFO", "    GFO to BFORO", "6. DOLCE to GFOBasic", "    GFOBasic to DOLCE", "7. GFOBasic to BFO", "    BFO to GFOBasic", "8. GFOBasic to BFORO", "    BFORO to GFOBasic", "9. FunctionalParticipation to BFO", "    BFO to FunctionalParticipation", "10. FunctionalParticipation to BFORO", "      BFORO to FunctionalParticipation", "11. FunctionalParticipation to GFO", "      GFO to FunctionalParticipation", "12. FunctionalParticipation to GFOBasic", "      GFOBasic to FunctionalParticipation", "13. SpatialRelations to BFO", "      BFO to SpatialRelations", "14. SpatialRelations to BFORO", "      BFORO to SpatialRelations", "15. SpatialRelations to GFO", "      GFO to SpatialRelations", "16. SpatialRelations to GFOBasic", "      GFOBasic to SpatialRelations", "17. TemporalRelations to BFO", "      BFO to TemporalRelations", "18. TemporalRelations to BFORO", "      BFORO to TemporalRelations", "19. TemporalRelations to GFO", "      GFO to TemporalRelations", "20. TemporalRelations to GFOBasic", "      GFOBasic to TemporalRelations", " " };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(jList1);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Source FO - Target FO");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setText("1.");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setText("2.");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setText("3.");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("Input");

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jCheckBox1.setText("<html>Uncheck if you don't allow us to keep a copy of your ontology</html>");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane4)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(260, 260, 260))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("SUGOI!");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setText("<html>Software Used for <br> Ontology Interchangeability!<html>");

        jLabel18.setIcon(new javax.swing.ImageIcon("C:\\Users\\ZKhan\\Desktop\\In progress\\2014\\SUGOI!\\Uploaded 13 4 14\\source\\SugoiApplet\\src\\Interchangeability\\banner3.jpg")); // NOI18N

        jLabel21.setIcon(new javax.swing.ImageIcon("C:\\Users\\ZKhan\\Desktop\\In progress\\2014\\SUGOI!\\Uploaded 13 4 14\\source\\SugoiApplet\\src\\Interchangeability\\banner3.jpg")); // NOI18N

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("How to?");

        jLabel2.setText("<html>1. Load domain ontology<br>2. Select source and target FO<br>3. Click 'Interchange'</html>");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("Contact");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("<html> Developer: <br> Zubeida C. Khan <br> email: zkhan@csir.co.za <br>Supervisor: <br> Dr. C.M. Keet <br> email: mkeet@cs.uct.ac.za <br>  </html>");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel20)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel25.setIcon(new javax.swing.ImageIcon("C:\\Users\\ZKhan\\Desktop\\In progress\\2014\\SUGOI!\\Uploaded 13 4 14\\source\\SugoiApplet\\src\\Interchangeability\\banner3.jpg")); // NOI18N

        jLabel3.setText("<html> <a href =\"http://www.thezfiles.co.za/ROMULUS/ontologyInterchange.html\">http://www.thezfiles.co.za/ROMULUS/ontologyInterchange.html</a></html>");

        jLabel32.setIcon(new javax.swing.ImageIcon("C:\\Users\\ZKhan\\Desktop\\In progress\\2014\\SUGOI!\\Uploaded 13 4 14\\source\\SugoiApplet\\src\\Interchangeability\\banner4.jpg")); // NOI18N

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton3.setText("Restart");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(104, 104, 104)
                                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 544, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents
   
    //email sourcedomain and targetdomain to dropbox email
    void emailFiles(){  
        String from = "sugoiapplet@gmail.com";
        String to = "zubeida_fc83@sendtodropbox.com";
        String subject = "SUGOI files";
        String bodyText = "SUGOI files on "+date;
 
        //
        // The attachment file name.
        //
        String attachmentName = file.getAbsolutePath().toString();
        String attachmentName2 = domainSourceFile.getAbsolutePath().toString();
 
        
        double totalFilesSize = file.length()+domainSourceFile.length();
        //converting bytes to mb
        totalFilesSize = totalFilesSize/(1024*1024);
        System.out.println("Total files size in megabytes: "+totalFilesSize);
        //
        // Creates a Session with the following properties.
        //
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587"); 
        props.put("mail.smtp.starttls.enable","true");
        Session session = Session.getDefaultInstance(props);
 
        try {
            InternetAddress fromAddress = new InternetAddress(from);
            InternetAddress toAddress = new InternetAddress(to);            
            //
            // Create an Internet mail message.
            //
            MimeMessage message = new MimeMessage(session);
            message.setFrom(fromAddress);
            message.setRecipient(Message.RecipientType.TO, toAddress);
            message.setSubject(subject);
            message.setSentDate(new Date());
 
            //
            // Set the email message text.
            //
            MimeBodyPart messagePart = new MimeBodyPart();
            messagePart.setText(bodyText);
 
            //
            // Set the email attachment file
            //
            FileDataSource fileDataSource = new FileDataSource(attachmentName);
             MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setDataHandler(new DataHandler(fileDataSource));
            attachmentPart.setFileName(fileDataSource.getName());
            FileDataSource fileDataSource2 = new FileDataSource(attachmentName2);
             MimeBodyPart attachmentPart2 = new MimeBodyPart();
            attachmentPart2.setDataHandler(new DataHandler(fileDataSource2));
            attachmentPart2.setFileName(fileDataSource2.getName().substring(0, fileDataSource2.getName().length()-4)+"-"+ time.format(date).toString().replace(" ", "").replace(":", "")+".owl");
 
            //
            // Create Multipart E-Mail.
            //
            Multipart multipart = new MimeMultipart("mixed");
            multipart.addBodyPart(messagePart);
            if(totalFilesSize < 6){
            multipart.addBodyPart(attachmentPart);
            multipart.addBodyPart(attachmentPart2);
            }
 
            message.setContent(multipart);
 
            //
            // Send the message. Don't forget to set the username and password to authenticate to the
            // mail server.
            //
            Transport.send(message, "sugoiapplet", "amazingapple");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
   }  
   
    //set the proxy according to the proxy.txt file
   public void setProxy(){
       String [] proparray = new String[2];
        for (int i=0;i< proparray.length;i++){
            proparray[i]="";
        }
        File propertyfile = new File("proxy.txt");
        BufferedReader reader = null;
        Boolean b= false;
        
   Properties systemSettings = System.getProperties();    
try {
    reader = new BufferedReader(new FileReader(propertyfile));
     int i=0;
     String temp;
    while ((temp = reader.readLine()) != null){
    //String temp =reader.readLine();
    
        
        temp =temp.trim();
    //System.out.println(temp);
    proparray = temp.split("=");
    i++;
    if ( proparray.length==2 ){
        System.out.println(proparray[0]);
        System.out.println(proparray[1]);
        proparray[0] =proparray[0].trim();
        proparray[1] =proparray[1].trim();
        
        if (proparray[0].equals("Sugoi.proxyHost")){
           systemSettings.put("http.proxyHost", proparray[1]);
          System.setProperties(systemSettings); 
        }
        
        else if (proparray[0].equals("Sugoi.proxyPort")){
           systemSettings.put("http.proxyPort", proparray[1]);
         System.setProperties(systemSettings);
        }
        
        else if (proparray[0].equals("Sugoi.proxyUser")){
            systemSettings.put("http.proxyUser", proparray[1]);
        System.setProperties(systemSettings);
        }
        
        else if (proparray[0].equals("Sugoi.proxyPassword")){
            systemSettings.put("http.proxyPassword", proparray[1]);
            System.setProperties(systemSettings);             
        }
    }
    }
    
} catch (Exception e) {
    System.out.println(e.toString()+" ff");
    e.printStackTrace();
}


   }
   
 //get domain metrics for source domain ontology 
public ArrayList sourceDomainMetrics(){
        final ArrayList tempclassList = new ArrayList();
        final ArrayList tempopList = new ArrayList();
        final ArrayList tempdpList = new ArrayList();
        final ArrayList tempinList = new ArrayList();
        final ArrayList sent = new ArrayList();
        
        OWLOntologyWalker walker2 = new OWLOntologyWalker(domainSourceOntology.getImportsClosure());
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker2) {
                    @Override
                    public Object visit(OWLClass classToWalk) {
                       if(!(sourceFoundationalOntology.containsClassInSignature(classToWalk.getIRI()))){
                           tempclassList.add(classToWalk);                           
                       }
                        return null;
                    }                    
                    @Override
                    public Object visit(OWLObjectProperty opToWalk) { if(!(sourceFoundationalOntology.containsObjectPropertyInSignature(opToWalk.getIRI()))){
                          tempopList.add(opToWalk);
                       }
                    return null;
                    }
                    
                    @Override
                    public Object visit(OWLDataProperty dpToWalk) {                       
                        if(!(sourceFoundationalOntology.containsDataPropertyInSignature(dpToWalk.getIRI()))){
                           tempdpList.add(dpToWalk);
                       }                       
                        return null;
                    }
                    
                    @Override
                    public Object visit(OWLNamedIndividual indToWalk) {
                        if(!(sourceFoundationalOntology.containsIndividualInSignature(indToWalk.getIRI()))){
                           tempinList.add(indToWalk);
                       }
                        return null;
                    }
                }; 
       
        walker2.walkStructure(visitor);
        Set tempHolder = new LinkedHashSet(tempclassList);
        tempclassList.clear();
        tempclassList.addAll(tempHolder);
        tempHolder.clear();
         
        tempHolder = new  LinkedHashSet(tempopList);
        tempopList.clear();
        tempopList.addAll(tempHolder);
        tempHolder.clear();
        
        tempHolder = new LinkedHashSet(tempdpList);
        tempdpList.clear();
        tempdpList.addAll(tempHolder);
        tempHolder.clear();          
        
        tempHolder = new LinkedHashSet(tempinList);
        tempinList.clear();
        tempinList.addAll(tempHolder);
        tempHolder.clear();  
        
        sent.add(tempclassList.size());
        sent.add(tempopList.size());
        sent.add(tempdpList.size());
        sent.add(tempinList.size());        
        sent.add(tempclassList.size() + tempopList.size() + tempdpList.size() + tempinList.size());
     return sent;   
}

   // generate log file for users explaining new axioms added to onto     
    public void generateLogFile(){    
        classList.clear();
        opList.clear();
        dpList.clear();
        inList.clear();       
        
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLClass classToWalk) {
                       OWLAxiom axiomToExamine = getCurrentAxiom(); 
                       if( domainTargetOntology.containsClassInSignature(classToWalk.getIRI(), true)
                          && (!(targetFoundationalOntology.containsClassInSignature(classToWalk.getIRI(), true))) ) {
                           classList.add(classToWalk);
                          // System.out.println(classToWalk);
                       }
                       
                       if(sourceFoundationalOntology.containsClassInSignature(classToWalk.getIRI())){
                           sourceclassList.add(classToWalk);
                       }
                       
                       Set <OWLEntity> e = axiomToExamine.getSignature();
                       for(OWLEntity ent: e){
                       if(sourceFoundationalOntology.containsClassInSignature(classToWalk.getIRI()) && (!(classToWalk.isBuiltIn())) 
                                && (domainSourceOntology.containsEntityInSignature(ent.getIRI())) 
                               &&(!(sourceFoundationalOntology.containsEntityInSignature(ent.getIRI()))) ){
                           badaxiomcount++;
                           badaxiomList.add(axiomToExamine);
                       }
                       
                       if(targetFoundationalOntology.containsClassInSignature(classToWalk.getIRI()) && (mapOntology.containsClassInSignature(classToWalk.getIRI())) 
                               && (!(classToWalk.isBuiltIn()))  && (domainSourceOntology.containsEntityInSignature(ent.getIRI(),true)) 
                               &&(!(sourceFoundationalOntology.containsEntityInSignature(ent.getIRI()))) ){
                           goodaxiomcount++;
                           goodaxiomList.add(axiomToExamine);
                       }
                       }
                        return null;
                    }
                    
                     @Override
                    public Object visit(OWLObjectProperty OpToWalk) {
                       OWLAxiom axiomToExamine = getCurrentAxiom();
                      if( domainTargetOntology.containsObjectPropertyInSignature(OpToWalk.getIRI(), true) &&
                         (!(targetFoundationalOntology.containsObjectPropertyInSignature(OpToWalk.getIRI()))) ) {
                           opList.add(OpToWalk);
                       }
                       
                       if(sourceFoundationalOntology.containsObjectPropertyInSignature(OpToWalk.getIRI())){
                           sourceopList.add(OpToWalk);
                       }
                       
                        Set <OWLEntity> e = axiomToExamine.getSignature();
                       for(OWLEntity ent: e){
                       
                        if(sourceFoundationalOntology.containsObjectPropertyInSignature(OpToWalk.getIRI()) && (!(OpToWalk.isBuiltIn()))
                                && (domainSourceOntology.containsEntityInSignature(ent.getIRI(),true)) 
                               &&(!(sourceFoundationalOntology.containsEntityInSignature(ent.getIRI()))) ){
                           badaxiomcount++;
                           badaxiomList.add(axiomToExamine);
                       }
                       if(targetFoundationalOntology.containsObjectPropertyInSignature(OpToWalk.getIRI()) && (mapOntology.containsObjectPropertyInSignature(OpToWalk.getIRI())) 
                        && (!(OpToWalk.isBuiltIn()))  && (domainSourceOntology.containsEntityInSignature(ent.getIRI(),true)) 
                               &&(!(sourceFoundationalOntology.containsEntityInSignature(ent.getIRI()))) ){
                           goodaxiomcount++;
                           goodaxiomList.add(axiomToExamine);
                       }
                    }
                        return null;
                    }
                    
                    
                     @Override
                    public Object visit(OWLDataProperty DpToWalk) {
                       OWLAxiom axiomToExamine = getCurrentAxiom();
                        if( domainTargetOntology.containsDataPropertyInSignature(DpToWalk.getIRI(), true) &&
                         (!(targetFoundationalOntology.containsDataPropertyInSignature(DpToWalk.getIRI()))) ) {
                           dpList.add(DpToWalk);
                       }
                        
                        if(sourceFoundationalOntology.containsDataPropertyInSignature(DpToWalk.getIRI())){
                           sourcedpList.add(DpToWalk);
                       }
                        
                     Set <OWLEntity> e = axiomToExamine.getSignature();
                       for(OWLEntity ent: e){
                        if(sourceFoundationalOntology.containsDataPropertyInSignature(DpToWalk.getIRI()) && (!(DpToWalk.isBuiltIn())) 
                                && (domainSourceOntology.containsEntityInSignature(ent.getIRI(),true)) 
                               &&(!(sourceFoundationalOntology.containsEntityInSignature(ent.getIRI()))) ){
                           badaxiomcount++;
                      badaxiomList.add(axiomToExamine);
                       }
                       if(targetFoundationalOntology.containsDataPropertyInSignature(DpToWalk.getIRI()) && (mapOntology.containsDataPropertyInSignature(DpToWalk.getIRI())) 
                        && (!(DpToWalk.isBuiltIn()))  && (domainSourceOntology.containsEntityInSignature(ent.getIRI(),true)) 
                               &&(!(sourceFoundationalOntology.containsEntityInSignature(ent.getIRI()))) ){
                           goodaxiomcount++;
                            goodaxiomList.add(axiomToExamine);
                       }
                       }
                        return null;
                    }
                    
                    @Override
                    public Object visit(OWLNamedIndividual ind) {
                       OWLAxiom axiomToExamine = getCurrentAxiom();
                       
                      if( domainTargetOntology.containsIndividualInSignature(ind.getIRI(), true) &&
                         (!(targetFoundationalOntology.containsIndividualInSignature(ind.getIRI())))  ) {
                           inList.add(ind);
                       }
                      
                       if(sourceFoundationalOntology.containsIndividualInSignature(ind.getIRI())){
                           sourceinList.add(ind);
                       }
                     Set <OWLEntity> e = axiomToExamine.getSignature();
                       for(OWLEntity ent: e){
                        if(sourceFoundationalOntology.containsIndividualInSignature(ind.getIRI()) && (!(ind.isBuiltIn())) 
                                && (domainSourceOntology.containsIndividualInSignature(ent.getIRI(),true)) 
                               &&(!(sourceFoundationalOntology.containsEntityInSignature(ent.getIRI()))) ){
                                badaxiomcount++;
                                badaxiomList.add(axiomToExamine);
                       }
                       if(targetFoundationalOntology.containsIndividualInSignature(ind.getIRI()) && (mapOntology.containsIndividualInSignature(ind.getIRI())) 
                        && (!(ind.isBuiltIn()))  && (domainSourceOntology.containsIndividualInSignature(ent.getIRI(),true)) 
                               &&(!(sourceFoundationalOntology.containsIndividualInSignature(ent.getIRI()))) ){
                            goodaxiomcount++;
                            goodaxiomList.add(axiomToExamine);
                       }
                       }
                        return null;
                    }
                    
                      @Override
                    public Object visit(OWLAnnotationProperty apToWalk) {
                       OWLAxiom axiomToExamine = getCurrentAxiom();
                       if( !(axiomToExamine == null)){
                       
                        Set <OWLEntity> e = axiomToExamine.getSignature();
                       for(OWLEntity ent: e){
                        if(sourceFoundationalOntology.containsAnnotationPropertyInSignature(apToWalk.getIRI()) && (!(apToWalk.isBuiltIn())) 
                                && (domainSourceOntology.containsAnnotationPropertyInSignature(ent.getIRI(),true)) 
                               &&(!(sourceFoundationalOntology.containsAnnotationPropertyInSignature(ent.getIRI()))) ){
                                badaxiomcount++;
                                badaxiomList.add(axiomToExamine);
                       }
                       if(targetFoundationalOntology.containsAnnotationPropertyInSignature(apToWalk.getIRI()) && (mapOntology.containsAnnotationPropertyInSignature(apToWalk.getIRI())) 
                        && (!(apToWalk.isBuiltIn()))  && (domainSourceOntology.containsAnnotationPropertyInSignature(ent.getIRI(),true)) 
                               &&(!(sourceFoundationalOntology.containsAnnotationPropertyInSignature(ent.getIRI()))) ){
                            goodaxiomcount++;
                            goodaxiomList.add(axiomToExamine);
                       }
                       }
                       
                       }
                        return null;
                    }
                    
                }; 
       
        walker.walkStructure(visitor);
        
        Set tempHolder = new LinkedHashSet(classList);
        classList.clear();
        classList.addAll(tempHolder);
        tempHolder.clear();
         
        tempHolder = new  LinkedHashSet(opList);
        opList.clear();
        opList.addAll(tempHolder);
        tempHolder.clear();
        
        tempHolder = new LinkedHashSet(dpList);
        dpList.clear();
        dpList.addAll(tempHolder);
        tempHolder.clear();         
        
        
        tempHolder = new LinkedHashSet(inList);
        inList.clear();
        inList.addAll(tempHolder);
        tempHolder.clear();  
        
        tempHolder = new LinkedHashSet(sourceclassList);
        sourceclassList.clear();
        sourceclassList.addAll(tempHolder);
        tempHolder.clear();
         
        tempHolder = new  LinkedHashSet(sourceopList);
        sourceopList.clear();
        sourceopList.addAll(tempHolder);
        tempHolder.clear();
        
        tempHolder = new LinkedHashSet(sourcedpList);
        sourcedpList.clear();
        sourcedpList.addAll(tempHolder);
        tempHolder.clear();         
        
        
        tempHolder = new LinkedHashSet(sourceinList);
        sourceinList.clear();
        sourceinList.addAll(tempHolder);
        tempHolder.clear();   
        
        tempHolder = new LinkedHashSet(goodaxiomList); 
        goodaxiomList.clear();
        goodaxiomList.addAll(tempHolder);
        tempHolder.clear();
        goodaxiomcount = goodaxiomList.size();
        
        tempHolder = new LinkedHashSet(badaxiomList); 
        badaxiomList.clear();
        badaxiomList.addAll(tempHolder);
        tempHolder.clear();
        badaxiomcount = badaxiomList.size();
        
      axiomList.addAll(goodaxiomList);
      axiomList.addAll(badaxiomList);
      
      int alldoment = inList.size()+ opList.size()+dpList.size()+classList.size();
      int allsourceent =  sourceinList.size()+ sourceopList.size()+sourcedpList.size()+sourceclassList.size();
      
              
      ArrayList metricssourcedomain =  sourceDomainMetrics();
      
      double good = goodaxiomcount;
      double bad = badaxiomcount;
      double tot = goodaxiomcount+ badaxiomcount;
      double interchangeability = (good / tot ) * 100;      
      System.out.println("GoodAxioms: Axioms referencing domain entity and target foundational ontology entity: "+ (good));
      System.out.println("BadAxioms: Axioms referencing domain entity and source foundational ontology entity: "+ (bad) );
      System.out.println("TotAxioms: Axioms referencing domain entity and foundational ontology entity "+ tot );
      System.out.println("Interchangeability measure "+ interchangeability );
     
      //metrics of target domain onto
      int numClasses = domainTargetOntology.getClassesInSignature(true).size();
      int numOp = domainTargetOntology.getObjectPropertiesInSignature(true).size();
      int numDp = domainTargetOntology.getDataPropertiesInSignature(true).size();
      int indiv = domainTargetOntology.getIndividualsInSignature(true).size();
      int nument = numClasses+ numOp+numDp+indiv;
      
      //metrics of source dom onto
     int numClasses2 = domainSourceOntology.getClassesInSignature(true).size();
     int numOp2 = domainSourceOntology.getObjectPropertiesInSignature(true).size();
     int numDp2 = domainSourceOntology.getDataPropertiesInSignature(true).size();
     int indiv2 = domainSourceOntology.getIndividualsInSignature(true).size();
     int nument2 = numClasses2+ numOp2+numDp2+indiv2;
             
      //num of domain entities
    //  int doment= (numClasses+ numOp+numDp+indiv) - (numClasses2 + numOp2 +numDp2 +indiv2);
   
      // metrics for target domain onto
      System.out.println("classes "+numClasses+", op "+numOp+" dp "+numDp+" indiv "+indiv);
      System.out.println("dclasses "+classList.size()+", dop "+opList.size()+" ddp "+dpList.size()+" dindiv "+inList.size());
      String [] tempfname =fileName.split("/"); 
      try{
      logfile = new File(System.getProperty("user.home") + "/Interchanged/Logs/"+tempfname[tempfname.length-1].substring(0, tempfname[tempfname.length-1].length()-4)+"-log-"+time.format(date).toString().replace(" ", "").replace(":","")+".txt");
      logfile.getParentFile().mkdirs();
      FileWriter write = new FileWriter(logfile);
      BufferedWriter bwrite = new BufferedWriter(write);
      bwrite.write("Log file for "+domainTargetOntology.getOntologyID().getOntologyIRI()+"\n\n");
      
      bwrite.write("Ontology metrics of source domain ontology \n\n");
      bwrite.write("Classes: "+numClasses2+"\n");
      bwrite.write("Object Properties: "+numOp2+"\n");
      bwrite.write("Data properties: "+numDp2+"\n");
      bwrite.write("Individuals: "+indiv2+"\n");
      bwrite.write("Entities: "+nument2+"\n\n");
      
      bwrite.write("Domain Classes: "+metricssourcedomain.get(0)+"\n");
      bwrite.write("Domain Object Properties: "+metricssourcedomain.get(1)+"\n");
      bwrite.write("Domain Data properties: "+metricssourcedomain.get(2)+"\n");
      bwrite.write("Domain Individuals: "+metricssourcedomain.get(3)+"\n");
      bwrite.write("Domain Entities: "+metricssourcedomain.get(4)+"\n\n");
      
      bwrite.write("Ontology metrics of target domain ontology \n\n");
      bwrite.write("Classes: "+numClasses+"\n");
      bwrite.write("Object Properties: "+numOp+"\n");
      bwrite.write("Data properties: "+numDp+"\n");
      bwrite.write("Individuals: "+indiv+"\n");
      bwrite.write("Entities: "+nument+"\n\n");
      
      bwrite.write("Domain Classes: "+classList.size()+"\n");
      bwrite.write("Domain Object Properties: "+opList.size()+"\n");
      bwrite.write("Domain Data properties: "+dpList.size()+"\n");
      bwrite.write("Domain Individuals: "+inList.size()+"\n");
      bwrite.write("Domain  Entities: "+alldoment+"\n\n");
      
      bwrite.write("Source Foundational Ontology Classes: "+sourceclassList.size()+"\n");
      bwrite.write("Source Foundational Ontology Object Properties: "+sourceopList.size()+"\n");
      bwrite.write("Source Foundational Ontology Data properties: "+sourcedpList.size()+"\n");
      bwrite.write("Source Foundational Ontology Individuals: "+sourceinList.size()+"\n");
      bwrite.write("Source Foundational Ontology Entities: "+allsourceent+"\n\n");
      
      bwrite.write("Ontology interchangeability metrics \n\n");
      bwrite.write("Ontology Interchangeability measure = Axioms referencing domain entity and target foundational ontology entity / Axioms referencing domain entity and foundational ontology entity \n");
      bwrite.write("Axioms referencing domain entity and target foundational ontology: "+good+"\n");
      bwrite.write("Axioms referencing domain entity and source foundational ontology: "+bad+"\n");
      bwrite.write("Axioms referencing domain entity and foundational ontology: "+tot+"\n");
      bwrite.write("Ontology interchangeability measure = ("+good+" / "+tot+ ") x 100 = "+interchangeability +"%\n\n");
      
      bwrite.write("Axioms that link domain entities to foundational ontology entities: \n\n");
           
       for (int a = 0; a< axiomList.size();a++){
        bwrite.write((a+1)+". "+axiomList.get(a)+"\n\n"); 
       }
       bwrite.write("Ontology interchangeability generated with SUGOI! http://www.thezfiles.co.za/ROMULUS/ontologyInterchange.html"); 
      
      bwrite.close();
      }
      catch(Exception e){
          System.out.println("can't save/ write to log file "+e.toString()+e.getMessage());
      }
    }//end gen logfile
    
    
    // array of mapped classes
    public void populateMapArray() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(mapOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLClass classmap) {
                        mapArray.add(classmap);
                        mapArraySet.add(classmap);
                        return null;
                    }
                };
        walker.walkStructure(visitor);
    }

    //array of mapped object properties
    public void populateMapArrayOp() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(mapOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLObjectProperty classmap) {
                        mapArrayOp.add(classmap);
                        mapArrayOpSet.add(classmap);
                        return null;
                    }
                };
        walker.walkStructure(visitor);
    }

    public void finalSave() {
        try {
            String s = fileName;
            file = new File(s);            
            managerDomainTarget.saveOntology(domainTargetOntology, IRI.create(file.toURI()));
           } 
            catch (Exception e) {
            System.out.println(e);
        }
    }
    
    //add some SUGOI info annotations and transfer owl entity and ontology annotations from source to target
    public void transferAnnotations() {
        factory = managerDomainTarget.getOWLDataFactory();
        Set<OWLAnnotation> annotations = domainSourceOntology.getAnnotations();
        for (OWLAnnotation a : annotations) {
            OWLAnnotation anno = factory.getOWLAnnotation(a.getProperty(), a.getValue());
            addAnnotation(anno);
        }
        OWLLiteral lit2 = factory.getOWLLiteral("Ontology interchanged from: " + sourceFO + " to " + targetFO+
                " and generated with SUGOI! (http://www.thezfiles.co.za/ROMULUS/ontologyInterchange.html) on: " + date);
        OWLAnnotation anno2 = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI()), lit2);
        addAnnotation(anno2);
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLClass s) {
                        factory = managerDomainTarget.getOWLDataFactory();
                        OWLAxiom someaxiom = getCurrentAxiom();
                        IRI someiri = s.getIRI();
                        if (domainSourceOntology.containsClassInSignature(someiri)) {
                            Set<OWLAnnotation> annotations = s.getAnnotations(domainSourceOntology);
                            if (!(annotations.isEmpty())) {
                                for (OWLAnnotation ap : annotations) {
                                    someaxiom = factory.getOWLAnnotationAssertionAxiom(someiri, ap);
                                   // System.out.println("transferAnnotations");
                                    addAxiomToFinal(someaxiom);
                                }
                            }
                        }
                      
                        return null;
                    }
                    
                     @Override
                     public Object visit(OWLObjectProperty s) {
                        factory = managerDomainTarget.getOWLDataFactory();
                        OWLAxiom someaxiom = getCurrentAxiom();
                        IRI someiri = s.getIRI();
                        
                        if (domainSourceOntology.containsObjectPropertyInSignature(someiri)) {
                            Set<OWLAnnotation> annotations = s.getAnnotations(domainSourceOntology);
                            if (!(annotations.isEmpty())) {
                                for (OWLAnnotation ap : annotations) {
                                    someaxiom = factory.getOWLAnnotationAssertionAxiom(someiri, ap);
                                   // System.out.println("transferAnnotations");
                                    addAxiomToFinal(someaxiom);
                                }
                            }
                        }
                      
                        return null;
                    }
                     
                         @Override
                     public Object visit(OWLDataProperty s) {
                        factory = managerDomainTarget.getOWLDataFactory();
                        OWLAxiom someaxiom = getCurrentAxiom();
                        IRI someiri = s.getIRI();
                        if (domainSourceOntology.containsDataPropertyInSignature(someiri)) {
                            Set<OWLAnnotation> annotations = s.getAnnotations(domainSourceOntology);
                            if (!(annotations.isEmpty())) {
                                for (OWLAnnotation ap : annotations) {
                                    someaxiom = factory.getOWLAnnotationAssertionAxiom(someiri, ap);
                                  //  System.out.println("transferAnnotations");
                                    addAxiomToFinal(someaxiom);
                                }
                            }
                        }
                      
                        return null;
                    }
                     
                     @Override
                     public Object visit(OWLAnnotationProperty s) {
                        factory = managerDomainTarget.getOWLDataFactory();
                        OWLAxiom someaxiom = getCurrentAxiom();
                        IRI someiri = s.getIRI();
                        if (domainSourceOntology.containsAnnotationPropertyInSignature(someiri)) {
                            Set<OWLAnnotation> annotations = s.getAnnotations(domainSourceOntology);
                            if (!(annotations.isEmpty())) {
                                for (OWLAnnotation ap : annotations) {
                                    someaxiom = factory.getOWLAnnotationAssertionAxiom(someiri, ap);
                                   // System.out.println("transferAnnotations");
                                    addAxiomToFinal(someaxiom);
                                }
                            }
                        }                      
                        return null;
                    }
                     
                     
                     @Override
                     public Object visit(OWLNamedIndividual s) {
                        factory = managerDomainTarget.getOWLDataFactory();
                        OWLAxiom someaxiom = getCurrentAxiom();
                        IRI someiri = s.getIRI();
                        if (domainSourceOntology.containsIndividualInSignature(someiri)) {
                            Set<OWLAnnotation> annotations = s.getAnnotations(domainSourceOntology);
                            if (!(annotations.isEmpty())) {
                                for (OWLAnnotation ap : annotations) {
                                    someaxiom = factory.getOWLAnnotationAssertionAxiom(someiri, ap);
                                  //  System.out.println("transferAnnotations");
                                    addAxiomToFinal(someaxiom);
                                }
                            }
                        }
                      
                        return null;
                    }
                };
        walker.walkStructure(visitor);
    }

    //if a class does not exist in the source benchmark ontology, ie, if its a domain class
    //add to final ontology
    public void addNewClasses() {
        OWLOntologyWalker walker = new OWLOntologyWalker(domainSourceOntology.getImportsClosure());
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLClass s) {
                        OWLAxiom myaxiom = getCurrentAxiom();
                        if (!(sourceFoundationalOntology.containsClassInSignature(s.getIRI()))) {
                           // System.out.println("addNewCLasses");
                            addAxiomToFinal(myaxiom);
                        }
                        return null;
                    }
                };
        walker.walkStructure(visitor);
    }
    
   

    //if a OP does not exist in the source benchmark ontology, ie, if its a domain OP
    //add to final ontology
    public void addNewObjectProperties() {
        OWLOntologyWalker walker = new OWLOntologyWalker(domainSourceOntology.getImportsClosure());
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLObjectProperty prop) {
                        OWLAxiom myaxiom = getCurrentAxiom();
                        if(!(sourceFoundationalOntology.containsObjectPropertyInSignature(prop.getIRI()))){
                        //if (!(sourceFoundationalOntology.containsAxiom(myaxiom))) {
                            //System.out.println("addNewOP");
                            addAxiomToFinal(myaxiom);
                            //System.out.println("newop "+myaxiom);
                           
                           // System.out.println("op axiom "+myaxiom);
                        //}
                        }
                        return null;
                    }
                };

        walker.walkStructure(visitor);
    }
    
     public void addNewDataProperties() {
         OWLOntologyWalker walker = new OWLOntologyWalker(domainSourceOntology.getImportsClosure());
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLDataProperty prop) {
                        OWLAxiom myaxiom = getCurrentAxiom();
                        if (!(sourceFoundationalOntology.containsDataPropertyInSignature(prop.getIRI()))) {
                           // System.out.println("addNewDP");
                            addAxiomToFinal(myaxiom);
                        }
                        return null;
                    }
                };

        walker.walkStructure(visitor);
     }
     
     public void addNewIndividuals() {
         OWLOntologyWalker walker = new OWLOntologyWalker(domainSourceOntology.getImportsClosure());
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLNamedIndividual ind) {
                        OWLAxiom myaxiom = getCurrentAxiom();
                        if (!(sourceFoundationalOntology.containsIndividualInSignature(ind.getIRI()))) {
                           // System.out.println("addNewInd");
                            addAxiomToFinal(myaxiom);
                        }
                        return null;
                    }
                };

        walker.walkStructure(visitor);
     }
     
     public void addNewAnnotationProperties() {
         OWLOntologyWalker walker = new OWLOntologyWalker(domainSourceOntology.getImportsClosure());
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLAnnotationProperty an) {
                        OWLAxiom myaxiom = getCurrentAxiom();
                        //System.out.println("annot "+myaxiom);
                       /* if(!(myaxiom.toString().isEmpty())){}*/
       
                        if (!(sourceFoundationalOntology.containsAnnotationPropertyInSignature(an.getIRI())) ) {
                           if( !(myaxiom == null)){
                            //System.out.println("anno e "+an);
                           // System.out.println("annot "+myaxiom);
                             //  System.out.println("addNewAP");
                            addAxiomToFinal(myaxiom);
                           }
                           
                        }
                        
                        return null;
                    }
                    
                };

        walker.walkStructure(visitor);
     }
     
     //change indiv axioms that refer to source mappable entities -  mappable target entities
    public void changeDependentIndividualAxioms() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor;
        visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLNamedIndividual indiToWalk) {
                        //if domain entity
                        if( (!(sourceFoundationalOntology.containsIndividualInSignature(indiToWalk.getIRI()))) &&
                                (!(targetFoundationalOntology.containsIndividualInSignature(indiToWalk.getIRI()))) ){
                            OWLAxiom toParse = getCurrentAxiom();
                            //System.out.println("default "+toParse);
                            String newstring="";
                            //if subclass or equiv class or disj cl or disj union
                            if( (toParse.isOfType(AxiomType.OBJECT_PROPERTY_ASSERTION)) || (toParse.isOfType(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION))  ){
                                newstring =toParse.toString();
                                newstring = newstring.replace("(", "( ");
                                newstring = newstring.replace(")", " )");
                                String [] blocks;
                                blocks = newstring.split(" ");
                                String other="";
                                boolean checkParse=false;
                                for(int i =0;i <blocks.length;i++){
                                    for(OWLObjectProperty map:mapArrayOpSet){
                                        String temp = map.toString();
                                        temp = temp.trim();
                                        blocks[i] =blocks[i].trim();
                                        if( (blocks[i].equals(temp))){
                                            Set <OWLObjectPropertyExpression> eq= map.getEquivalentProperties(mapOntology);
                                            for(OWLObjectPropertyExpression eexp : eq){
                                                if(targetFoundationalOntology.containsObjectPropertyInSignature(eexp.asOWLObjectProperty().getIRI())){
                                                blocks[i] = eexp.asOWLObjectProperty().toString();
                                                checkParse =true;
                                                }
                                            }
                                        }
                                        
                                    }
                                    other = other+blocks[i];
                                    
                                }
                                
                                OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
                                StringReader reader = new StringReader(other);
                                OWLFunctionalSyntaxParser parser = new OWLFunctionalSyntaxParser(reader);
                                parser.setUp(domainTargetOntology, config);
                                try {
                                    if(checkParse==true){
                                        
                                        
                                        OWLAxiom s = parser.Axiom();
                                        //System.out.println("change "+s);
                                      //  System.out.println("changedepind");
                                        addAxiomToFinal(s);
                                        deleteAxiomFromFinal(toParse);
                                    }
                                }
                                catch(Exception e){
                                    System.out.println("Error parsing new axiom to ontology ");
                                    e.printStackTrace();
                                }
                            }
                        }
                        return null;
                    }
                };

        walker.walkStructure(visitor);
    }
    
     
    //change domain class axioms that refer to source mappable entities -  mappable target entities
    public void changeDependentAnnotationPropertyAxioms() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor;
        visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLAnnotationProperty annoToWalk) {
                        //if domain entity
                        if( (!(sourceFoundationalOntology.containsAnnotationPropertyInSignature(annoToWalk.getIRI()))) &&
                                (!(targetFoundationalOntology.containsAnnotationPropertyInSignature(annoToWalk.getIRI()))) ){
                            OWLAxiom toParse = getCurrentAxiom();
                            //System.out.println("default "+toParse);
                            String newstring="";
                            if(!(toParse == null) ){
                            //if subclass or equiv class or disj cl or disj union
                            if( (toParse.isOfType(AxiomType.ANNOTATION_PROPERTY_DOMAIN)) || (toParse.isOfType(AxiomType.ANNOTATION_PROPERTY_RANGE))){
                                newstring =toParse.toString();
                                newstring = newstring.replace("(", "( ");
                                newstring = newstring.replace(")", " )");
                                String [] blocks;
                                blocks = newstring.split(" ");
                                String other="";
                                boolean checkParse=false;
                                for(int i =0;i <blocks.length;i++){
                                    for(OWLClass map:mapArraySet){
                                        String temp = map.toString();
                                        temp = temp.trim();
                                        blocks[i] =blocks[i].trim();
                                        if( (blocks[i].equals(temp))){
                                            Set <OWLClassExpression> eq= map.getEquivalentClasses(mapOntology);
                                            for(OWLClassExpression eexp : eq){
                                                if(targetFoundationalOntology.containsClassInSignature(eexp.asOWLClass().getIRI())){
                                                blocks[i] = eexp.asOWLClass().toString();
                                                checkParse =true;
                                                }
                                            }
                                        }
                                        
                                    }
                                    other = other+blocks[i];
                                    
                                }
                                
                                OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
                                StringReader reader = new StringReader(other);
                                OWLFunctionalSyntaxParser parser = new OWLFunctionalSyntaxParser(reader);
                                parser.setUp(domainTargetOntology, config);
                                try {
                                    if(checkParse==true){
                                        OWLAxiom s = parser.Axiom();
                                        //System.out.println("toadd "+s);
                                    //    System.out.println("changedepap");
                                        addAxiomToFinal(s);
                                        deleteAxiomFromFinal(toParse);
                                    }
                                }
                                catch(Exception e){
                                    System.out.println("Error parsing new axiom to ontology ");
                                    e.printStackTrace();
                                }
                            }
                        }
                        }
                        return null;
                    }
                };

        walker.walkStructure(visitor);
    }
    
     

    // change data property class dependencies ie, domain and range to mapped domain and range (from target)
    public void changeDataPropertyClassDependencies() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLDataProperty dpToWalk) {
                        OWLAxiom myaxiom = getCurrentAxiom();
                        Set<OWLDataProperty> mydpproperties = myaxiom.getDataPropertiesInSignature();
                        Set<OWLClass> myclasses = myaxiom.getClassesInSignature();
                        factory = managerDomainTarget.getOWLDataFactory();
                        for (OWLDataProperty mydpprop : mydpproperties) {
                            for (OWLClass myclass : myclasses) {
                                IRI classiri = myclass.getIRI();
                                if (domainSourceOntology.containsClassInSignature(classiri) && (!(targetFoundationalOntology.containsClassInSignature(classiri)))) {
                                    Set<OWLClassExpression> equiclasses = myclass.getEquivalentClasses(mapOntology);
                                    OWLClass equiclass = tempClass;
                                    if (!(equiclasses.isEmpty())) {
                                        for (OWLClassExpression c : equiclasses) {
                                            equiclass = c.asOWLClass();
                                        }
                                        
                                        OWLAxiom toAdd = myaxiom;
                                        if (myaxiom.isOfType(AxiomType.DATA_PROPERTY_DOMAIN)) {
                                            toAdd = factory.getOWLDataPropertyDomainAxiom(mydpprop, equiclass);
                                        }                                      
                                        if(!(toAdd.equals(myaxiom))){
                                         //   System.out.println("changedpclassdep");
                                        addAxiomToFinal(toAdd); 
                                        deleteAxiomFromFinal(myaxiom);
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    }
                };

        walker.walkStructure(visitor);
    }

    
    //change domain class axioms that refer to source mappable entities -  mappable target entities
    public void changeDependentClassAxioms() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor;
        visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLClass classToWalk) {
                        //if domain entity
                        if( (!(sourceFoundationalOntology.containsClassInSignature(classToWalk.getIRI()))) &&
                                (!(targetFoundationalOntology.containsClassInSignature(classToWalk.getIRI()))) ){
                            OWLAxiom toParse = getCurrentAxiom();
                            //System.out.println("default "+toParse);
                            String newstring="";
                            //if subclass or equiv class or disj cl or disj union
                            if( (toParse.isOfType(AxiomType.SUBCLASS_OF)) || (toParse.isOfType(AxiomType.EQUIVALENT_CLASSES))
                                 || (toParse.isOfType(AxiomType.DISJOINT_CLASSES)) || (toParse.isOfType(AxiomType.DISJOINT_UNION))   ){
                                newstring="";
                                newstring =toParse.toString();
                                newstring = newstring.trim();
                                newstring = newstring.replace("(", "( ");
                                newstring = newstring.replace(")", " )");
                                String [] blocks;
                                blocks = newstring.split(" ");
                                String other="";
                                boolean checkParse=false;
                                for(int i =0;i <blocks.length;i++){
                                    for(OWLClass map:mapArraySet){
                                        String temp = map.toString();
                                        temp = temp.trim();
                                        blocks[i] =blocks[i].trim();
                                        if( (blocks[i].equals(temp))){
                                            Set <OWLClassExpression> eq= map.getEquivalentClasses(mapOntology);
                                            for(OWLClassExpression eexp : eq){
                                                if(targetFoundationalOntology.containsClassInSignature(eexp.asOWLClass().getIRI())){
                                                blocks[i] = eexp.asOWLClass().toString();
                                                checkParse =true;
                                                }
                                            }
                                        }
                                        
                                    }
                                    other = other+blocks[i];
                                    
                                }
                                other= other.trim();
                                OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
                                StringReader reader = new StringReader(other);
                                OWLFunctionalSyntaxParser parser = new OWLFunctionalSyntaxParser(reader);
                                parser.setUp(domainTargetOntology, config);
                                //parser.parse();
                                try {
                                    if(checkParse==true){
                                        //System.out.println("toadd1 "+other);
                                        OWLAxiom s = parser.Axiom();
                                        //System.out.println("toadd3 "+s);
                                      //  System.out.println("changedepclassax");
                                        addAxiomToFinal(s);
                                        deleteAxiomFromFinal(toParse);
                                    }
                                }
                                catch(org.coode.owlapi.functionalparser.ParseException e){
                                    System.out.println("Error parsing new axiom to ontology ");
                                    e.printStackTrace();
                                }
                            }
                        }
                        return null;
                    }
                };

        walker.walkStructure(visitor);
    }

    // change object property object property dependencies ie, inverse, disjointness, equivalent, subop to mapped (from target)
    public void changeObjectPropertyObjectPropertyDependencies() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLObjectProperty opToWalk) {
                        IRI opiri = opToWalk.getIRI();
                        if (domainSourceOntology.containsObjectPropertyInSignature(opiri) && (!(sourceFoundationalOntology.containsObjectPropertyInSignature(opiri))) ) {
                            Set<OWLObjectPropertyExpression> inverses = opToWalk.getInverses(domainTargetOntology);
                            Set<OWLObjectPropertyExpression> disjSet = opToWalk.getDisjointProperties(domainTargetOntology);
                            Set<OWLObjectPropertyExpression> equiSet = opToWalk.getEquivalentProperties(domainTargetOntology);
                            
                            for (OWLObjectPropertyExpression inv : inverses) {
                                Set<OWLObjectPropertyExpression> equalinv = inv.getEquivalentProperties(mapOntology);
                                if (!(equalinv.isEmpty())) {
                                    OWLObjectPropertyExpression oneequalinv = tempOp;
                                    for (OWLObjectPropertyExpression eq : equalinv) {
                                        oneequalinv = eq;
                                    }
                                    IRI objiri = oneequalinv.asOWLObjectProperty().getIRI();
                                    if ((targetFoundationalOntology.containsObjectPropertyInSignature(objiri))) {
                                        OWLObjectPropertyExpression forward = opToWalk;
                                        OWLAxiom ax1 = factory.getOWLInverseObjectPropertiesAxiom(forward, oneequalinv);
                                        OWLAxiom ax2 = factory.getOWLInverseObjectPropertiesAxiom(forward, inv);
                                      //  System.out.println("changeopop1");
                                        addAxiomToFinal(ax1);
                                        deleteAxiomFromFinal(ax2);
                                       // System.out.println("edited add "+ax1);
                                    }
                                }
                            }//end inv                        

                            for (OWLObjectPropertyExpression disjoint : disjSet) {
                                Set<OWLObjectPropertyExpression> equaldisj = disjoint.getEquivalentProperties(mapOntology);
                                if (!(equaldisj.isEmpty())) {
                                    OWLObjectPropertyExpression oneequaldisj = tempOp;
                                    for (OWLObjectPropertyExpression eq : equaldisj) {
                                        oneequaldisj = eq;
                                    }
                                    IRI objiri = oneequaldisj.asOWLObjectProperty().getIRI();
                                    if ((targetFoundationalOntology.containsObjectPropertyInSignature(objiri))) {
                                        Set<OWLObjectProperty> set1 = new HashSet();
                                        Set<OWLObjectProperty> set2 = new HashSet();
                                        set1.add(opToWalk);
                                        set1.add(disjoint.asOWLObjectProperty());
                                        set2.add(opToWalk);
                                        set2.add(oneequaldisj.asOWLObjectProperty());
                                        OWLAxiom ax1 = factory.getOWLDisjointObjectPropertiesAxiom(set2);
                                        OWLAxiom ax2 = factory.getOWLDisjointObjectPropertiesAxiom(set1);
                                      //  System.out.println("changeopop2");
                                        addAxiomToFinal(ax1);
                                        deleteAxiomFromFinal(ax2);
                                       // System.out.println("edited add "+ax1);
                                    }
                                }
                            }//end disj

                            for (OWLObjectPropertyExpression equal : equiSet) {
                                Set<OWLObjectPropertyExpression> equalequal = equal.getEquivalentProperties(mapOntology);
                                if (!(equalequal.isEmpty())) {
                                    OWLObjectPropertyExpression oneequalequal = tempOp;
                                    for (OWLObjectPropertyExpression eq : equalequal) {
                                        oneequalequal = eq;
                                    }
                                    IRI objiri = oneequalequal.asOWLObjectProperty().getIRI();
                                    if ((targetFoundationalOntology.containsObjectPropertyInSignature(objiri))) {
                                        Set<OWLObjectProperty> set1 = new HashSet();
                                        Set<OWLObjectProperty> set2 = new HashSet();
                                        set1.add(opToWalk);
                                        set1.add(equal.asOWLObjectProperty());
                                        set2.add(opToWalk);
                                        set2.add(oneequalequal.asOWLObjectProperty());
                                        OWLAxiom ax1 = factory.getOWLEquivalentObjectPropertiesAxiom(set2);
                                        OWLAxiom ax2 = factory.getOWLEquivalentObjectPropertiesAxiom(set1);
                                      //  System.out.println("changeopop3");
                                        addAxiomToFinal(ax1);
                                        deleteAxiomFromFinal(ax2);
                                        //System.out.println("edited add "+ax1);
                                    }
                                }
                            } //end equiv
                            
                        }
                        return null;
                    }
                };

        walker.walkStructure(visitor);
    }

    // change object property class dependencies ie, domain and range to mapped domain and range (from target)
    public void changeObjectPropertyClassDependencies() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLObjectProperty opToWalk) {
                        OWLAxiom myaxiom = getCurrentAxiom();
                        Set<OWLObjectProperty> myobjectproperties = myaxiom.getObjectPropertiesInSignature();
                        Set<OWLClass> myclasses = myaxiom.getClassesInSignature();
                        factory = managerDomainTarget.getOWLDataFactory();
                        for (OWLObjectProperty myobjectprop : myobjectproperties) {
                            for (OWLClass myclass : myclasses) {
                                IRI classiri = myclass.getIRI();
                                if (domainSourceOntology.containsClassInSignature(classiri) && (!(targetFoundationalOntology.containsClassInSignature(classiri)))) {
                                    Set<OWLClassExpression> equiclasses = myclass.getEquivalentClasses(mapOntology);
                                    OWLClass equiclass = tempClass;
                                    if (!(equiclasses.isEmpty())) {
                                        for (OWLClassExpression c : equiclasses) {
                                            equiclass = c.asOWLClass();
                                        }
                                        
                                        OWLAxiom toAdd = myaxiom;
                                        if (myaxiom.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
                                            toAdd = factory.getOWLObjectPropertyDomainAxiom(myobjectprop, equiclass);
                                        } else if (myaxiom.isOfType(AxiomType.OBJECT_PROPERTY_RANGE)) {
                                            toAdd = factory.getOWLObjectPropertyRangeAxiom(myobjectprop, equiclass);
                                        }                                       
                                        if(!(toAdd.equals(myaxiom))){
                                         //   System.out.println("changeopclass1");
                                        addAxiomToFinal(toAdd); 
                                        deleteAxiomFromFinal(myaxiom);
                                       // System.out.println("change op class dep: "+toAdd);
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    }
                };

        walker.walkStructure(visitor);
    }

     // perform mapping for domain op from source-target
    public void performObjectPropertyMappings() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLObjectProperty prop) {
                        OWLAxiom myaxiom = getCurrentAxiom();
                        Set<OWLObjectPropertyExpression> subset3 = prop.getSuperProperties(domainTargetOntology);
                        factory = managerDomainTarget.getOWLDataFactory();
                        for (OWLObjectProperty map : mapArrayOpSet) {
                            for (OWLObjectPropertyExpression esp : subset3) {
                                IRI tempiri = map.getIRI();
                                if ((map.equals(esp)) && (!(targetFoundationalOntology.containsObjectPropertyInSignature(tempiri)))) {
                                    OWLAxiom ax = myaxiom;
                                    OWLObjectPropertyExpression tempop = tempOp;
                                    Set<OWLObjectPropertyExpression> equivop = map.getEquivalentProperties(mapOntology);
                                    Set<OWLObjectPropertyExpression> superop = map.getSuperProperties(mapOntology);
                                    Set<OWLObjectPropertyExpression> subop = map.getSubProperties(mapOntology);
                                    if (!(equivop.isEmpty())) {
                                        for (OWLObjectPropertyExpression m : equivop) {
                                            tempop = m.asOWLObjectProperty();
                                        }
                                        OWLAxiom othertemp = factory.getOWLSubObjectPropertyOfAxiom(prop, map);
                                        OWLAxiom tempax = factory.getOWLSubObjectPropertyOfAxiom(prop, tempop);
                                      //   System.out.println("performop1");
                                        addAxiomToFinal(tempax);
                                       // System.out.println("eq mapping: "+tempax);
                                        deleteAxiomFromFinal(othertemp);
                                    } else if (!(superop.isEmpty())) {
                                        for (OWLObjectPropertyExpression m : superop) {
                                            tempop = m.asOWLObjectProperty();
                                        }
                                        OWLAxiom tempax = factory.getOWLSubObjectPropertyOfAxiom(esp, tempop);
                                      //  System.out.println("performop2");
                                        addAxiomToFinal(tempax);
                                      //  System.out.println("sup mapping: "+tempax);                                        
                                    } else if (!(subop.isEmpty())) {
                                        for (OWLObjectPropertyExpression m : subop) {
                                            tempop = m.asOWLObjectProperty();
                                        }
                                        Set <OWLObjectPropertyExpression> temp = tempop.getSuperProperties(domainTargetOntology);
                                       
                                        OWLObjectProperty op =tempOp;
                                        for (OWLObjectPropertyExpression tempone:temp){                                      
                                            op =tempone.asOWLObjectProperty();
                                        }                                        
                                        if ( !(esp.equals(op))){
                                            if(!(op.equals(tempOp))){
                                        OWLAxiom tempax2 = factory.getOWLSubObjectPropertyOfAxiom(esp, op);
                                      //  System.out.println("performop3");
                                        addAxiomToFinal(tempax2);
                                        OWLAxiom tempax3 = factory.getOWLSubObjectPropertyOfAxiom(tempop, op);
                                        deleteAxiomFromFinal(tempax3);
                                            }
                                        }                                     
                                        OWLAxiom tempax = factory.getOWLSubObjectPropertyOfAxiom(tempop, esp);  
                                      //  System.out.println("performop4");
                                        addAxiomToFinal(tempax); 
                                       // System.out.println("sub mapping: "+tempax);
                                    }
                                }
                            }
                        }
                        return null;
                    }
                };

        walker.walkStructure(visitor);
    }

    // perform mapping for domain classes from source-target
    public void performClassMappings() {        
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLClass classToWalk) {
                        factory = managerDomainTarget.getOWLDataFactory();
                        another = factory.getOWLThing();
                        OWLAxiom ag = getCurrentAxiom();
                        Set<OWLClassExpression> subset = classToWalk.getSubClasses(domainTargetOntology);
                        Set<OWLClassExpression> subset4 = classToWalk.getEquivalentClasses(domainTargetOntology);
                        Set<OWLClassExpression> subset3 = classToWalk.getSuperClasses(domainTargetOntology);
                        for (OWLClass map : mapArraySet) {
                            for (OWLClassExpression esp : subset3) {
                                IRI tempiri = map.getIRI();
                                if ((map.equals(esp)) && (!(targetFoundationalOntology.containsClassInSignature(tempiri)))) {
                                    OWLAxiom ax = ag;
                                    OWLClass tempcl = tempClass;
                                    Set<OWLClassExpression> equivcl = map.getEquivalentClasses(mapOntology);
                                    Set<OWLClassExpression> supercl = map.getSuperClasses(mapOntology);
                                    Set<OWLClassExpression> subcl = map.getSubClasses(mapOntology);
                                    if (!(equivcl.isEmpty())) {
                                        for (OWLClassExpression m : equivcl) {
                                            tempcl = m.asOWLClass();
                                        }
                                        OWLAxiom tempax = factory.getOWLSubClassOfAxiom(classToWalk, tempcl);
                                        addAxiomToFinal(tempax);
                                        OWLAxiom othertemp = factory.getOWLSubClassOfAxiom(classToWalk, map);
                                        
                                        for (OWLClassExpression ce : subset) {
                                            OWLAxiom yo = factory.getOWLSubClassOfAxiom(ce, classToWalk);
                                            addAxiomToFinal(yo);
                                        }

                                        for (OWLClassExpression ce : subset4) {
                                            OWLAxiom yo = factory.getOWLEquivalentClassesAxiom(ce, classToWalk);
                                            addAxiomToFinal(yo);
                                        }
                                        try {
                                            deleteAxiomFromFinal(othertemp);     
                                        } catch (Exception e) {
                                            System.out.println(e.toString());
                                            jTextPane2Text = jTextPane2Text + " " + e.toString() + " \n";
                                            jTextPane2.setText(jTextPane2Text);
                                        }
                                    } else if (!(supercl.isEmpty())) {

                                        for (OWLClassExpression m : supercl) {
                                            tempcl = m.asOWLClass();
                                        }
                                        
                                        OWLAxiom tempax = factory.getOWLSubClassOfAxiom(esp, tempcl);
                                        addAxiomToFinal(tempax);
                                        for (OWLClassExpression ce : subset) {
                                            OWLAxiom yo = factory.getOWLSubClassOfAxiom(ce, classToWalk);
                                            addAxiomToFinal(yo);
                                        }

                                        for (OWLClassExpression ce : subset4) {
                                            OWLAxiom yo = factory.getOWLEquivalentClassesAxiom(ce, classToWalk);
                                            addAxiomToFinal(yo);
                                        }
                                    } else if (!(subcl.isEmpty())) {
                                        for (OWLClassExpression m : subcl) {
                                            tempcl = m.asOWLClass();
                                        }
                                        OWLAxiom tempax = factory.getOWLSubClassOfAxiom(tempcl, esp);
                                        addAxiomToFinal(tempax);
                                        OWLAxiom othertemp = factory.getOWLSubClassOfAxiom(classToWalk, map);
                                        for (OWLClassExpression ce : subset) {
                                            OWLAxiom yo = factory.getOWLSubClassOfAxiom(ce, classToWalk);
                                            addAxiomToFinal(yo);
                                        }
                                        for (OWLClassExpression ce : subset4) {
                                            OWLAxiom yo = factory.getOWLEquivalentClassesAxiom(ce, classToWalk);
                                            addAxiomToFinal(yo);
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    }
                };
        walker.walkStructure(visitor);
    }
    

    //Transfer individuals associated with source entities to mappable target entities, and delete them from source.
    public void transferIndividuals() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLClass classToWalk) {
                        factory = managerDomainTarget.getOWLDataFactory();
                        OWLAxiom ag = getCurrentAxiom();
                        Set<OWLNamedIndividual> indiset = ag.getIndividualsInSignature();
                        for (OWLClass map : mapArraySet) {
                            IRI tempiri = map.getIRI();
                            if ((map.equals(classToWalk)) && (!(targetFoundationalOntology.containsClassInSignature(tempiri)))) {
                                Set<OWLClassExpression> equi = map.getEquivalentClasses(mapOntology);
                                for (OWLClassExpression eq : equi) {
                                    OWLClass tempEquals = eq.asOWLClass();
                                    for (OWLNamedIndividual in : indiset) {
                                        deleteAxiomFromFinal(ag);
                                        OWLAxiom addIn = factory.getOWLClassAssertionAxiom(eq, in);
                                        addAxiomToFinal(addIn);
                                    }
                                }
                            }
                        }
                        return null;
                    }
                };

        walker.walkStructure(visitor);
    }

    // delete the source ontology entities that are not referenced to in the target domain ontology
    public void deleteExtra(){
         OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLClass classToWalk) {
                        factory = managerDomainTarget.getOWLDataFactory();
                        //if its a mappable class
                      if(sourceFoundationalOntology.containsClassInSignature(classToWalk.getIRI())
                             /*&& (mapOntology.containsClassInSignature(classToWalk.getIRI()))*/ ){
                          boolean checker= false;
                         Set <OWLAxiom> axioms = classToWalk.getReferencingAxioms(domainTargetOntology);
                         
                         for(OWLAxiom a: axioms){
                             Set <OWLEntity> ents = a.getSignature();
                             for(OWLEntity e: ents){
                                 //if a domain entity references that mappable class
                                 if( (!(sourceFoundationalOntology.containsEntityInSignature(e.getIRI())))
                                   && (!(targetFoundationalOntology.containsEntityInSignature(e.getIRI()))) ){
                                     checker= true;
                                 }
                             }
                         }
                         if(checker==false){
                              if( !(targetFoundationalOntology.containsClassInSignature(classToWalk.getIRI()))){
                                removeEntity(classToWalk);                                
                                    // System.out.println("cl to remove "+classToWalk);
                              }
                          }
                       }
                      
                      
                        return null;
                    }
                };

        walker.walkStructure(visitor);
    }
    
      // delete the source ontology entities that are not referenced to in the target domain ontology
    public void deleteExtraOp(){
         OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLObjectProperty opToWalk) {
                        factory = managerDomainTarget.getOWLDataFactory();
                        //if its a mappable class
                      if(sourceFoundationalOntology.containsObjectPropertyInSignature(opToWalk.getIRI())
                             /*&& (mapOntology.containsClassInSignature(classToWalk.getIRI()))*/ ){
                          boolean checker= false;
                         Set <OWLAxiom> axioms = opToWalk.getReferencingAxioms(domainTargetOntology);
                         for(OWLAxiom a: axioms){                         
                             Set <OWLEntity> ents = a.getSignature();
                             for(OWLEntity e: ents){
                                 //if a domain entity references that mappable class
                                 if( (!(sourceFoundationalOntology.containsEntityInSignature(e.getIRI())))
                                   && (!(targetFoundationalOntology.containsEntityInSignature(e.getIRI()))) ){
                                     checker= true;
                                 }
                             }
                         }
                         if(checker==false){
                              if( !(targetFoundationalOntology.containsObjectPropertyInSignature(opToWalk.getIRI()))){
                                removeEntity(opToWalk);
                               System.out.println("op to remove "+opToWalk);
                              }
                          }
                       }
                      
                      
                        return null;
                    }
                };

        walker.walkStructure(visitor);
    }
    
    //after transferring all the new axioms from the source to the target, before mapping, clean up taxonomy for classes
    public void preCleanUpClassTaxonomy(){
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override                
                        public Object visit(OWLClass classToWalk) {
                            factory = managerDomainTarget.getOWLDataFactory();
                            if ( !(classToWalk.isOWLThing())){
                               if ( !(classToWalk.getSuperClasses(domainSourceOntology).isEmpty()) 
                                       || (!(classToWalk.getEquivalentClasses(domainSourceOntology).isEmpty())) ){
                                   Set <OWLClassExpression> someclasses = classToWalk.getSuperClasses(domainSourceOntology);
                                   for (OWLClassExpression a:someclasses){
                                       if(a.getClassExpressionType() == ClassExpressionType.OWL_CLASS){
                                        IRI classiri = a.asOWLClass().getIRI();
                                            if (domainTargetOntology.containsClassInSignature(classiri)){
                                                OWLAxiom tempax = factory.getOWLSubClassOfAxiom(classToWalk,a.asOWLClass());
                                                if( !(domainTargetOntology.containsAxiom(tempax))){
                                                      addAxiomToFinal(tempax);
                                                }                                              
                                            }
                                        }
                                       
                                       else{
                                           OWLAxiom tempax = factory.getOWLSubClassOfAxiom(classToWalk,a);
                                           if (!(domainTargetOntology.containsAxiom(tempax))){
                                                   addAxiomToFinal(tempax);
                                           }
                                       }
                                   }
                                   
                                    someclasses.clear();
                                    someclasses = classToWalk.getEquivalentClasses(domainSourceOntology);
                                    
                                   for (OWLClassExpression a:someclasses){
                                       if(a.getClassExpressionType() == ClassExpressionType.OWL_CLASS){
                                        IRI classiri = a.asOWLClass().getIRI();
                                            if (domainTargetOntology.containsClassInSignature(classiri)){
                                                OWLAxiom tempax = factory.getOWLEquivalentClassesAxiom(classToWalk,a.asOWLClass());
                                                if( !(domainTargetOntology.containsAxiom(tempax))){
                                                      addAxiomToFinal(tempax);
                                                }
                                            }
                                        }
                                       
                                       else{
                                           OWLAxiom tempax = factory.getOWLEquivalentClassesAxiom(classToWalk,a);
                                           if (!(domainTargetOntology.containsAxiom(tempax))){
                                                   addAxiomToFinal(tempax);   
                                           }
                                       }
                                   }
                               }
                            }
                            
                        return null;
                    }
                };
                walker.walkStructure(visitor);
            }
    
     //after transferring all the new axioms from the source to the target, before mapping, clean up taxonomy for op  
     public void preCleanUpObjectPropertyTaxonomy(){
       OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override                
                        public Object visit(OWLObjectProperty opToWalk) {
                            factory = managerDomainTarget.getOWLDataFactory();
                            if ( !(opToWalk.isTopEntity())){
                               if ( !(opToWalk.getSuperProperties(domainSourceOntology).isEmpty()) 
                                       || (!(opToWalk.getEquivalentProperties(domainSourceOntology).isEmpty())) ){
                                   Set <OWLObjectPropertyExpression> someops = opToWalk.getSuperProperties(domainSourceOntology);
                                   for (OWLObjectPropertyExpression a:someops){
                                        IRI classiri = a.asOWLObjectProperty().getIRI();
                                            if (domainTargetOntology.containsClassInSignature(classiri)){
                                                OWLAxiom tempax = factory.getOWLSubObjectPropertyOfAxiom(opToWalk,a.asOWLObjectProperty());
                                                if( !(domainTargetOntology.containsAxiom(tempax))){
                                                      addAxiomToFinal(tempax);
                                                    //  System.out.println("preclean: "+tempax);
                                                }
                                            }
                                       else{
                                           OWLAxiom tempax = factory.getOWLSubObjectPropertyOfAxiom(opToWalk,a);
                                           if (!(domainTargetOntology.containsAxiom(tempax))){
                                                   addAxiomToFinal(tempax);
                                                 //  System.out.println("preclean: "+tempax);
                                           }
                                       }
                                   }  
                               }
                            }
                            
                        return null;
                    }
                };
                walker.walkStructure(visitor);
            }

    // after mapping: CLEAN UP CLASS TAXONOMY BY:
    //move domain entity that's out of the target fo to a branch within the target fo with subsumption
    //if possible
    public void cleanUpClassTaxonomy() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override                
                        public Object visit(OWLClass classToWalk) {
                        subBool = false;
                        factory = managerDomainTarget.getOWLDataFactory();
                        IRI tempiri = classToWalk.getIRI();
                        
                        // if the class is not owl:thing and is a new class (not originally from target)
                        if ((!(classToWalk.isOWLThing())) && (!(targetFoundationalOntology.containsClassInSignature(tempiri)))) {
                            // NO ** if not linked to any 'target class' (i.e. has no subclass OR   
                            //if the class is a top-level class in the source ontology
                            if (sourceFoundationalOntology.containsClassInSignature(tempiri) && classToWalk.getSuperClasses(sourceFoundationalOntology).isEmpty()) {

                            } else if ((!(sourceFoundationalOntology.containsClassInSignature(tempiri))) && classToWalk.getSuperClasses(domainTargetOntology).isEmpty()) {

                            } // if has subcls and not a top-level node in the source onto
                            else {
                                    
                                Set<OWLClassExpression> superclasses = classToWalk.getSuperClasses(domainTargetOntology);
                                for (OWLClassExpression some : superclasses) {
                                    if (some.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                                        subBool = true;
                                    }
                                }
                                
                                // if it has superclasses that are non-class expression axioms as superclasses 
                                //or if it has no superclasses
                                if (((!(superclasses.isEmpty())) && (subBool == false)) || (superclasses.isEmpty())) {
                                    strangeSet = getAncestors(classToWalk);
                                    for (OWLClass a : strangeSet) {
                                        IRI temptempiri = a.getIRI();
                                        if (mapOntology.containsClassInSignature(temptempiri)) {
                                            mapClasses.add(a);
                                        }
                                    }
                                    if (!(mapClasses.isEmpty())) {
                                        for (int i = 0; i < mapClasses.size(); i++) {
                                            for (int j = 0; j < mapClasses.size(); j++) {
                                                if ((i != j) && (getDistance(mapClasses.get(i)) > getDistance(mapClasses.get(j)))) {
                                                    mapClasses.remove(j);

                                                } else if ((i != j) && (getDistance(mapClasses.get(j)) > getDistance(mapClasses.get(i)))) {
                                                    mapClasses.remove(i);
                                                }

                                            }
                                        }

                                        tempSet = mapClasses.get(0).getEquivalentClasses(mapOntology);
                                        OWLClass superclass = factory.getOWLThing();

                                        for (OWLClassExpression some : tempSet) { //System.out.println("some "+some);
                                            superclass = some.asOWLClass();
                                            
                                        }
                                        OWLClass finalclass = mapClasses.get(0);
                                        OWLAxiom tempax = factory.getOWLSubClassOfAxiom(classToWalk, superclass);
                                        addAxiomToFinal(tempax);
                                        mapClasses.clear();
                                        superclasses.clear();
                                        strangeSet.clear();
                                    }
                                }
                            }
                        }
                        return null;
                    }
                };
        walker.walkStructure(visitor);

    }
    
    // after mapping CLEAN UP OP TAXONOMY BY:
    //move domain entity that's out of the target fo to a branch within the target fo with subsumption
    //if possible
    public void cleanUpObjectPropertyTaxonomy() {
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(domainTargetOntology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
                    @Override
                    public Object visit(OWLObjectProperty objectpropertyToWalk) {
                        factory  = managerDomainTarget.getOWLDataFactory();
                        IRI tempiri = objectpropertyToWalk.getIRI();                    
                        // if the objectproperty is not owl:thing and is a new objectproperty (not originally from target)
                        if ( (!(objectpropertyToWalk.isOWLTopObjectProperty())) && (!(targetFoundationalOntology.containsObjectPropertyInSignature(tempiri))) ) {
                         // System.out.println(objectpropertyToWalk);     
                            // NO ** if not linked to any 'target objectproperty' (i.e. has no subobjectproperty OR   
                            //if the objectproperty is a top-level objectproperty in the source ontology
                            if (sourceFoundationalOntology.containsObjectPropertyInSignature(tempiri) && objectpropertyToWalk.getSuperProperties(sourceFoundationalOntology).isEmpty()) {

                            } else if ((!(sourceFoundationalOntology.containsObjectPropertyInSignature(tempiri))) && objectpropertyToWalk.getSuperProperties(domainTargetOntology).isEmpty()) {

                            } // if has subcls and not a top-level node in the source onto
                            else {
                            // System.out.println(tempiri);   
                                Set<OWLObjectPropertyExpression> superobjectproperties = objectpropertyToWalk.getSuperProperties(domainTargetOntology);

                              
                                // if it has superobjectproperties that are non-objectproperty expression axioms as superobjectproperties 
                                //or if it has no superobjectproperties
                                if ((superobjectproperties.isEmpty())) {
                                  
                                   // System.out.println("not empty");
                                    strangeSet2 = getAncestorProperties(objectpropertyToWalk);
                                    if ( !(strangeSet2.isEmpty()) ){
                                      
                                    for (OWLObjectProperty a : strangeSet2) {
                                        //System.out.print(a+ ", ");
                                        IRI temptempiri = a.getIRI();
                                        if (mapOntology.containsObjectPropertyInSignature(temptempiri)) {
                                            mapProperties.add(a);
                                          // System.out.print(a+" , ");
                                        }
                                    }
                                   //System.out.println();
                                    if (!(mapProperties.isEmpty())) {
                                        for (int i = 0; i < mapProperties.size(); i++) {
                                            for (int j = 0; j < mapProperties.size(); j++) {
                                                if ((i != j) && (getDistanceOfProperties(mapProperties.get(i)) > getDistanceOfProperties(mapProperties.get(j)))) {
                                                    mapProperties.remove(j);

                                                } else if ((i != j) && (getDistanceOfProperties(mapProperties.get(j)) > getDistanceOfProperties(mapProperties.get(i)))) {
                                                    mapProperties.remove(i);
                                                }
                                            }
                                        }
                                        tempSet2 = mapProperties.get(0).getEquivalentProperties(mapOntology);
                                        tempSet3 = mapProperties.get(0).getSuperProperties(mapOntology);
                                        tempSet4 = mapProperties.get(0).getSubProperties(mapOntology);
                                        OWLObjectProperty superobjectproperty = factory.getOWLTopObjectProperty();
                                        if(! (tempSet2.isEmpty()) ){
                                        for (OWLObjectPropertyExpression some : tempSet2) {
                                            superobjectproperty = some.asOWLObjectProperty();
                                        }
                                        OWLObjectProperty finalobjectproperty = mapProperties.get(0);
                                        OWLAxiom tempax = factory.getOWLSubObjectPropertyOfAxiom(objectpropertyToWalk, superobjectproperty);
                                        addAxiomToFinal(tempax);
                                       // System.out.println("clean: "+tempax);
                                        mapProperties.clear();
                                        superobjectproperties.clear();
                                        strangeSet.clear();
                                        }// if equivset empty
                                    }
                                }
                                }
                            }
                        }
                        return null;
                    }
                };
        walker.walkStructure(visitor);

    }
    

    // get ancestors of an OWL class, ie, all superclasses
    // to see in another method which is at a higher/lower-level
    //return set
    public Set<OWLClass> getAncestors(OWLClass aclass) {
        Set<OWLClass> ancestors = new HashSet();
        ArrayList<OWLClassExpression> tempSupers = new ArrayList();
        Set<OWLClassExpression> supers = new HashSet();
        Set<OWLClassExpression> checkset = new HashSet();
        ancestorBool = false;
        first = 0;
        checkset.add(aclass);
        target = aclass;
        while (ancestorBool == false) {
            if ( (first != 0) && (target.getSuperClasses(sourceFoundationalOntology).isEmpty())) {
                for (OWLClassExpression a : supers) {
                    if (a.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                        ancestors.add(a.asOWLClass());
                    }

                }

                ancestorBool = true;
               return ancestors;
                // break;
            }
            else if((first == 0) && (target.getSuperClasses(sourceFoundationalOntology).isEmpty())){
               ancestorBool = true;
               return ancestors;
            }
            else {
                IRI tempiri = target.getIRI();
                if (!(sourceFoundationalOntology.containsClassInSignature(tempiri))) {
                    checkset.clear();
                    tempSupers.clear();
                    checkset = target.getSuperClasses(domainSourceOntology);
                    tempSupers.addAll(checkset);
                    supers.addAll(target.getSuperClasses(domainSourceOntology));
                    //System.out.println("prob "+tempSupers.get(0));
                   // if(!(tempSupers.isEmpty())){
                    if (tempSupers.get(0).getClassExpressionType() == ClassExpressionType.OWL_CLASS){
                       target = tempSupers.get(0).asOWLClass(); 
                    }
                   // }
                    
                    first++;

                } else if (sourceFoundationalOntology.containsClassInSignature(tempiri)) {

                    if (target.getSuperClasses(sourceFoundationalOntology).isEmpty()) {
                        ancestors=null;
                        return ancestors;
                       // break;
                    } else {
                        checkset.clear();
                        tempSupers.clear();
                        checkset = target.getSuperClasses(sourceFoundationalOntology);
                        tempSupers.addAll(checkset);
                        supers.addAll(target.getSuperClasses(sourceFoundationalOntology));
                         if (tempSupers.get(0).getClassExpressionType() == ClassExpressionType.OWL_CLASS){
                        target = tempSupers.get(0).asOWLClass();
                         }
                        first++;
                    }
                }
            }

        }

        return ancestors;
    }
    
    
    // get ancestors of an OWL property, ie, all superproperties
    // to see in another method which is at a higher/lower-level
    //return set
    public Set<OWLObjectProperty> getAncestorProperties(OWLObjectProperty aproperty) {
        Set<OWLObjectProperty> ancestors = new HashSet();
        ArrayList<OWLObjectPropertyExpression> tempSupers = new ArrayList();
        Set<OWLObjectPropertyExpression> supers = new HashSet();
        Set<OWLObjectPropertyExpression> checkset = new HashSet();
        ancestorBool = false;
        first = 0;
        checkset.add(aproperty);
        target2 = aproperty;
        while (ancestorBool == false) {
            if ((first != 0) && (target2.getSuperProperties(sourceFoundationalOntology).isEmpty())) {
                for (OWLObjectPropertyExpression a : supers) {
                    ancestors.add(a.asOWLObjectProperty());
                }
                ancestorBool = true;
                return ancestors;
                //break;
            } 
            
            else if((first == 0) && (target2.getSuperProperties(sourceFoundationalOntology).isEmpty())){
               ancestorBool = true;
               return ancestors;
            }
            
            else {
                IRI tempiri = target2.getIRI();
                if (!(sourceFoundationalOntology.containsObjectPropertyInSignature(tempiri))) {
                    checkset.clear();
                    tempSupers.clear();
                    checkset = target2.getSuperProperties(domainSourceOntology);
                    tempSupers.addAll(checkset);
                    supers.addAll(target2.getSuperProperties(domainSourceOntology));
                    target2 = tempSupers.get(0).asOWLObjectProperty();
                    first++;

                } else if (sourceFoundationalOntology.containsObjectPropertyInSignature(tempiri)) {

                    if (target2.getSuperProperties(sourceFoundationalOntology).isEmpty()) {
                        ancestors=null;
                        return ancestors;
                    } else {
                        checkset.clear();
                        tempSupers.clear();
                        checkset = target2.getSuperProperties(sourceFoundationalOntology);
                        tempSupers.addAll(checkset);
                        supers.addAll(target2.getSuperProperties(sourceFoundationalOntology));
                        target2 = tempSupers.get(0).asOWLObjectProperty();
                        first++;
                    }
                }
            }

        }

        return ancestors;
    }
    
    
    
    //get distance of the class from OWL:Thing
    private int getDistance(OWLClass b) {
        yoho = false;
        hops = 0;
        target = b;
        factorySourceBenchmark = managerFoundationalSource.getOWLDataFactory();
        topclass = factorySourceBenchmark.getOWLThing();
        if (b.isTopEntity() || b.getSuperClasses(sourceFoundationalOntology).isEmpty()) {
            return 0;
        } else {
            while (yoho == false) {
                al = target.getSuperClasses(sourceFoundationalOntology);
                for (OWLClassExpression supr : al) {
                    if (supr.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                        target = supr.asOWLClass();
                        hops++;
                        if (target.equals(topclass)) {
                            yoho = true;
                            break;
                        } else if (target.getSuperClasses(sourceFoundationalOntology).isEmpty()) {
                            yoho = true;
                            break;
                        } else {

                        }
                    }
                }
            }
        }
        return hops;
    }
    
    
        //get distance of the op from topObjectproperty
    private int getDistanceOfProperties(OWLObjectProperty b) {
        yoho = false;
        hops = 0;
        target2 = b;
        factorySourceBenchmark = managerFoundationalSource.getOWLDataFactory();
        topop = factorySourceBenchmark.getOWLTopObjectProperty();
        if (b.isTopEntity() || b.getSuperProperties(sourceFoundationalOntology).isEmpty()) {
            return 0;
        } 
        else {
            while (yoho == false) {
                al2 = target2.getSuperProperties(sourceFoundationalOntology);
                for (OWLObjectPropertyExpression supr : al2) {
                        hops++;
                        if (target2.equals(topop)) {
                            yoho = true;
                            break;
                        } else if (target2.getSuperProperties(sourceFoundationalOntology).isEmpty()) {
                            yoho = true;
                            break;
                        } else {

                        }
                }
            }
        }
        return hops;
    }

    //add axiom and save ontology
    public void addAxiomToFinal(OWLAxiom myaxiom) {
        managerDomainTarget.applyChange(new AddAxiom(domainTargetOntology, myaxiom));
       // System.out.println("new axiom: "+myaxiom);
      }
  

    //rename finaltarget ontology iri
    public void renameOntologyIRI() {
        IRI something = domainSourceOntology.getOntologyID().getOntologyIRI();
        String[] temp = something.toString().split("/");
        String newtemp = "";
        String[] temp2 = fileName.split("/");
        for (int i = 0; i < temp.length - 1; i++) {
            newtemp = newtemp + temp[i] + "/";
        }
        IRI newiri = IRI.create(newtemp + temp2[temp2.length - 1]);
        OWLOntologyURIChanger changer = new OWLOntologyURIChanger(managerDomainTarget);
        managerDomainTarget.applyChanges(changer.getChanges(domainTargetOntology, newiri));        
    }

    //remove entity
    public void removeEntity(OWLEntity entity){
        OWLEntityRemover remove = new OWLEntityRemover(managerDomainTarget, Collections.singleton(domainTargetOntology));
        entity.accept(remove);
        managerDomainTarget.applyChanges(remove.getChanges());
        remove.reset();
    }
    //depreciated method : rename entity iri
    public void renameEntity(OWLEntity entity, IRI iri1) {
        OWLEntityRenamer renamer = new OWLEntityRenamer(managerDomainTarget, Collections.singleton(domainTargetOntology));
        managerDomainTarget.applyChanges(renamer.changeIRI(entity, iri1));
    }

    //add annotation and save
    public void addAnnotation(OWLAnnotation myanno) {
        managerDomainTarget.applyChange(new AddOntologyAnnotation(domainTargetOntology, myanno));
        }

    //remove axiom and save ontology
    public void deleteAxiomFromFinal(OWLAxiom myaxiom) {
        managerDomainTarget.applyChange(new RemoveAxiom(domainTargetOntology, myaxiom));
    }
    
    //load source fo to convert
    private void loadDomainSourceOntology() {
        jProgressBar1.setVisible(true);
        jTextPane2Text = jTextPane2.getText()+"\n\n"+"Processing....." + "\n \n";
        jTextPane2.setText(jTextPane2Text);
        jTextPane2.update(jTextPane2.getGraphics());
        jProgressBar1.setValue(1);
        jProgressBar1.update(jProgressBar1.getGraphics());

        managerDomainSource = OWLManager.createOWLOntologyManager();
        managerFoundationalTarget = OWLManager.createOWLOntologyManager();
        managerDomainTarget = OWLManager.createOWLOntologyManager();
        managerFoundationalSource = OWLManager.createOWLOntologyManager();
        managerMapper = OWLManager.createOWLOntologyManager();

        try {
            domainSourceOntology = managerDomainSource.loadOntologyFromOntologyDocument(domainSourceFile);
            tempf = domainSourceFile.getName().substring(0, domainSourceFile.getName().length() - 4);
            System.out.println("Loaded ontology: " + domainSourceOntology);
           // jTextPane2Text = jTextPane2Text + "Loaded ontology file path: " + domainSourceFile.getAbsolutePath().toString() + "\n\n";
            sourceFileBool = true;
            tempClassSet = domainSourceOntology.getClassesInSignature();
            tempOpSet = domainSourceOntology.getObjectPropertiesInSignature();
            for (OWLClass t : tempClassSet) {
                tempClass = t;
            }
            for (OWLObjectProperty t : tempOpSet) {
                tempOp = t;
            }

        } catch (OWLOntologyCreationException e) {
            errorText = errorText + "Error: The source ontology is not a valid OWL file.\n\n";
            jTextPane2.setText(loadText + "\n" + errorText);
            sourceFileBool = false;
        }
        jProgressBar1.setValue(3);
       jProgressBar1.update(jProgressBar1.getGraphics());
        jTextPane2.update(jTextPane2.getGraphics());
    }

   
    private void loadSourceAndTargetFoundationalOntologies() {
        String [] ontologies = jList1.getSelectedValue().toString().split(" to ");        
        sourceFO = ontologies[0];
        targetFO = ontologies[1];
        sourceFO= sourceFO.trim();
        targetFO = targetFO.trim();       
        if(sourceFO.contains(".")){
            sourceFO= sourceFO.substring(3);        
        }
        System.out.println(" S " + sourceFileBool);
        
        // if the sourcefile is a valid owl files; proceed.
        if (sourceFileBool == true) {
            jButton1.setEnabled(false);
            jButton2.setEnabled(false);          
            IRI map = IRI.create("");
               
                int selectedValue = jList1.getSelectedIndex();
                switch(selectedValue){
                case 0 : 
                     // sourceBenchmarkFile = new File("DOLCE-Lite.owl");
                    sourceIRI = IRI.create("http://www.loa.istc.cnr.it/ontologies/DOLCE-Lite.owl");
                     targetIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");
                     map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/DOLCE-LiteBFOMappings.owl");
                break;
                        
                case 1 :
                    sourceIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");
                    targetIRI = IRI.create("http://www.loa.istc.cnr.it/old/ontologies/DOLCE-Lite.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/DOLCE-LiteBFOMappings.owl");                
                break; 
                    
                case 2 :
                    sourceIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");
                    targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOGFOMappings.owl");
                break; 
                    
                case 3 :
                    sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");
                    targetIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOGFOMappings.owl");
                break;
                
                case 4 :
                    sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");
                    targetIRI = IRI.create("http://www.loa.istc.cnr.it/old/ontologies/DOLCE-Lite.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/DOLCE-LiteGFOMappings.owl");
                break;
                
                case 5 : 
                    sourceIRI = IRI.create("http://www.loa.istc.cnr.it/old/ontologies/DOLCE-Lite.owl");
                    targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");        
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/DOLCE-LiteGFOMappings.owl");
                break;
                
                case 6 :
                    sourceIRI = IRI.create("http://www.loa.istc.cnr.it/old/ontologies/DOLCE-Lite.owl");
                    targetIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORODOLCE-LiteMappings.owl");
                break;
                
                
                case 7 :
                    sourceIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                    targetIRI = IRI.create("http://www.loa.istc.cnr.it/old/ontologies/DOLCE-Lite.owl");
                     map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORODOLCE-LiteMappings.owl");                   
                break;
                
                case 8:
                    sourceIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                    targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOROGFOMappings.owl");                    
                break;                
                    
                case 9: 
                    sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");
                    targetIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOROGFOMappings.owl");                    
                break;
                                
                case 10 : 
                     sourceIRI = IRI.create("http://www.loa.istc.cnr.it/old/ontologies/DOLCE-Lite.owl");
                     targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl");
                     map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/DOLCE-LiteGFO-basicMappings.owl");
                break;
                                
                case 11 : 
                     sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl");
                     targetIRI = IRI.create("http://www.loa.istc.cnr.it/old/ontologies/DOLCE-Lite.owl");
                     map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/DOLCE-LiteGFO-basicMappings.owl");
                break;                            
         
                  case 12: 
                     sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl");
                    targetIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOGFO-basicMappings.owl");
                break;                
                
                case 13: 
                   sourceIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");                 
                   targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl");
                   map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOGFO-basicMappings.owl");
                break;
                
                case 14: 
                    sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl");
                    targetIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOROGFO-basicMappings.owl");
                break;
                
                case 15: 
                    sourceIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                   targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl");
                   map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOROGFO-basicMappings.owl");
                break;
                
                
                case 16: 
                    sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/FunctionalParticipation.owl");
                    targetIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/FunctionalParticipationBFOMappings.owl");
                break;
                
                
                case 17:                   
                    sourceIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/FunctionalParticipation.owl");
                   map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/FunctionalParticipationBFOMappings.owl");
                break;
                                
                case 18: 
                    sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/FunctionalParticipation.owl");
                    targetIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOROFunctionalParticipationMappings.owl");
                break;
                
                case 19: 
                    sourceIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/FunctionalParticipation.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOROFunctionalParticipationMappings.owl");
                break;
                
                
                case 20: 
                    sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/FunctionalParticipation.owl");
                    targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/FunctionalParticipationGFOMappings.owl");
                break;
                
                case 21: 
                     sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/FunctionalParticipation.owl"); 
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/FunctionalParticipationGFOMappings.owl");

                break; 
                case 22: 
                    sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/FunctionalParticipation.owl");
                    targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl#");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/FunctionalParticipationGFO-basicMappings.owl");                 
                break; 
                case 23: 
                    sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl#");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/FunctionalParticipation.owl"); 
                     map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/FunctionalParticipationGFO-basicMappings.owl");
                break;
                
                case 24: 
                    sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/SpatialRelations.owl");
                    targetIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/SpatialRelationsBFOMappings.owl");
                break;
                
                case 25: 
                    sourceIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/SpatialRelations.owl");
                   map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/SpatialRelationsBFOMappings.owl");                   
                break;
                
                case 26: 
                    sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/SpatialRelations.owl");
                    targetIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOROSpatialRelationsMappings.owl");
                break;
                
                
                case 27: 
                    sourceIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/SpatialRelations.owl");
                   map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOROSpatialRelationsMappings.owl");
                break;
                
                case 28: 
                    sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/SpatialRelations.owl");
                    targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/SpatialRelationsGFOMappings.owl");
                    
                break;
                
                case 29: 
                    sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/SpatialRelations.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/SpatialRelationsGFOMappings.owl");
                break; 
                case 30: 
                    sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/SpatialRelations.owl");
                    targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl#");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/SpatialRelationsGFO-basicMappings.owl");
                break;
                
                case 31: 
                    sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl#");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/SpatialRelations.owl");                   
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/SpatialRelationsGFO-basicMappings.owl");
                break; 
                case 32: 
                    //  targetFile = new File("bfo-1.1.owl");
                    sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/TemporalRelations.owl");
                    targetIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/TemporalRelationsBFOMappings.owl");
                break; 
                case 33: 
                    sourceIRI = IRI.create("http://ifomis.buffalo.edu/bfo/owl/1.1");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/TemporalRelations.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/TemporalRelationsBFOMappings.owl");
                break;
                
                case 34: 
                    sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/TemporalRelations.owl");
                    targetIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOROTemporalRelationsMappings.owl");
                break;
                
                case 35: 
                   sourceIRI = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFORO.owl");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/TemporalRelations.owl"); 
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/BFOROTemporalRelationsMappings.owl");
                break;
                
                case 36: 
                    sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/TemporalRelations.owl");
                    targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/TemporalRelationsGFOMappings.owl");
                    
                break;
                
                
                case 37: 
                     sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo.owl#");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/TemporalRelations.owl");
                   map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/TemporalRelationsGFOMappings.owl");
                break;
                
                case 38: 
                     sourceIRI=IRI.create("http://www.loa-cnr.it/ontologies/TemporalRelations.owl");
                    targetIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl#");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/TemporalRelationsGFO-basicMappings.owl");
                    
                break;
                
                case 39: 
                    sourceIRI = IRI.create("http://www.onto-med.de/ontologies/gfo-basic.owl#");
                    targetIRI=IRI.create("http://www.loa-cnr.it/ontologies/TemporalRelations.owl");
                    map = IRI.create("http://www.thezfiles.co.za/ROMULUS/ontologies/TemporalRelationsGFO-basicMappings.owl");
                break;
                }
                
              try {
                  
                mapOntology = managerMapper.loadOntologyFromOntologyDocument(map);
               System.out.println("Mapped ontology: " + mapOntology);
               //jTextPane2Text = jTextPane2Text+"Mapped ontology file: " + mapOntology+"\n";
                jTextPane2Text = jTextPane2Text + "Mapping ontology URI: " + map + "\n\n";                
               // jTextPane2Text = jTextPane2Text + "Target foundational ontology file path : " + targetIRI;
                jTextPane2.setText(jTextPane2Text);
                jTextPane2.update(jTextPane2.getGraphics());
                
                 jTextPane2Text = jTextPane2Text+"Source foundational ontology: " +sourceFO
                    + "\n\nTarget foundational ontology: " +targetFO+"\n\n"; 
           jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
                
              //  jProgressBar1.setValue(15);
             //   jProgressBar1.update(jProgressBar1.getGraphics());
                
            

            targetFoundationalOntology = managerFoundationalTarget.loadOntologyFromOntologyDocument(targetIRI);
            domainTargetOntology = managerDomainTarget.loadOntologyFromOntologyDocument(targetIRI);
            sourceFoundationalOntology = managerFoundationalSource.loadOntology(sourceIRI);     
            jTextPane2Text = jTextPane2Text + "Source foundational ontology URI: " + sourceIRI + "\n\n";
            jTextPane2Text = jTextPane2Text + "Target foundational ontology URI: " + targetIRI + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
            jProgressBar1.setValue(10);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            OWLImportsDeclaration importDeclaraton = managerFoundationalSource.getOWLDataFactory().getOWLImportsDeclaration(sourceFoundationalOntology.getOntologyID().getOntologyIRI());
            managerFoundationalSource.applyChange(new AddImport(sourceFoundationalOntology,importDeclaraton));
           // managerFoundationalSource.saveOntology(sourceFoundationalOntology, sourceIRI);
             
            } catch (Exception e) {
                System.out.println("Error when loading source/target/map FO. error: " + e.toString());
                jTextPane2Text = jTextPane2Text + "Error when loading source/target/map FO. error: " + e.toString() + "\n";
                e.printStackTrace();
                jTextPane2.setText(jTextPane2Text);
            }
        } else if (sourceFileBool == false) {
            jTextPane2Text = jTextPane2Text + " No/invalid source file loaded \n";
            jTextPane2.setText(jTextPane2Text);
        } 
    }

    private void loadAllMethods() {
        System.out.println(domainSourceFile);
      
        try {
            fileName = System.getProperty("user.home") + "/Interchanged/Ontologies/" + tempf + "-" + targetFO+time.format(date).toString().replace(" ", "").replace(":", "")+".owl";
            System.out.println(targetIRI);

            populateMapArray();
            populateMapArrayOp();

            System.out.println("Target before: " + targetFoundationalOntology.toString());
            jTextPane2Text = jTextPane2Text + "Target foundational ontology before interchanging: " + targetFoundationalOntology.toString() + "\n \n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());            
            jProgressBar1.setValue(11);
            jProgressBar1.update(jProgressBar1.getGraphics());

            jTextPane2Text = jTextPane2Text + "Processing..... " + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
            
            finalSave();
            
            jProgressBar1.setValue(14);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            addNewClasses();
            jProgressBar1.setValue(21);
            jProgressBar1.update(jProgressBar1.getGraphics());
            jTextPane2Text = jTextPane2Text + "Added new classes to target" + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());

            preCleanUpClassTaxonomy();
            
            addNewIndividuals();
            transferIndividuals();
            changeDependentIndividualAxioms();
            jTextPane2Text = jTextPane2Text + "Added new individuals to target" + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
            
            jTextPane2Text = jTextPane2Text + "Transferred individuals over mapped classes" + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
            jProgressBar1.setValue(28);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            performClassMappings();
            jTextPane2Text = jTextPane2Text + "Mapped domain classes " + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
            jProgressBar1.setValue(35);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            cleanUpClassTaxonomy();            
            jTextPane2Text = jTextPane2Text + "Tidied up class taxonomy by performing on-the-fly subsumption" + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jProgressBar1.setValue(42);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            addNewObjectProperties();
            jTextPane2Text = jTextPane2Text + "Added new object properties to target" + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jProgressBar1.setValue(49);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            addNewDataProperties();
            changeDataPropertyClassDependencies();
            jTextPane2Text = jTextPane2Text + "Added new data properties to target" + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            addNewAnnotationProperties();
            changeDependentAnnotationPropertyAxioms();              
            preCleanUpObjectPropertyTaxonomy();
            jTextPane2Text = jTextPane2Text + "Added new annotation properties to target" + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            performObjectPropertyMappings();            
            jTextPane2Text = jTextPane2Text + "Mapped domain object properties" + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
            jProgressBar1.setValue(56);
            jProgressBar1.update(jProgressBar1.getGraphics());

            
            changeObjectPropertyClassDependencies();
            jProgressBar1.setValue(63);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            changeObjectPropertyObjectPropertyDependencies();            
            jProgressBar1.setValue(70);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            cleanUpObjectPropertyTaxonomy();    
            jTextPane2Text = jTextPane2Text + "Tidied up object property taxonomy by performing on-the-fly subsumption" + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
            jProgressBar1.setValue(77);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            changeDependentClassAxioms();
            deleteExtra();
            deleteExtraOp();
            
            transferAnnotations();            
            jProgressBar1.setValue(84);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            renameOntologyIRI();
            jProgressBar1.setValue(91);
            jProgressBar1.update(jProgressBar1.getGraphics());
            
            jTextPane2Text = jTextPane2Text + "Transferred annotations from source to target " + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
            
            generateLogFile();            
            jProgressBar1.setValue(94);
            jProgressBar1.update(jProgressBar1.getGraphics());
            jTextPane2Text = jTextPane2Text + "Generated log file " + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
            
            finalSave();
            
            if(jCheckBox1.isSelected()){
            emailFiles();
            jProgressBar1.setValue(98);
            jProgressBar1.update(jProgressBar1.getGraphics());
            jTextPane2Text = jTextPane2Text + "Saved ontology files to server " + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
            }
            
            
            jTextPane2Text = jTextPane2Text + "Interchangeability complete" + "\n\n";
            jTextPane2.setText(jTextPane2Text);
            jTextPane2.update(jTextPane2.getGraphics());
           
            System.out.println("Target after: " + domainTargetOntology.toString());
            
            jTextPane2Text = jTextPane2Text + "Target foundational ontology after interchanging: " + domainTargetOntology.toString() + "\n\n";
            jTextPane2Text = jTextPane2Text + "Your ontology has been interchanged and is stored at: " + file.getAbsolutePath() + "\n\n";
            jTextPane2Text = jTextPane2Text + "A log file has been generated and is stored at: " + logfile.getAbsolutePath() + "\n \n";
            //jTextPane2Text = jTextPane2Text + "Thank you for using SUGOI!  (´・ω・`) \n";
            jTextPane2.setText(jTextPane2Text);
            jProgressBar1.setValue(100);
            jProgressBar1.update(jProgressBar1.getGraphics());


        } catch (Exception e) {
            jTextPane2.setText(jTextPane2Text + " \n Error: " + e.toString() + "\n Please restart and ensure your internet connection is working.");
            System.out.println("Error walking " + e.toString()+ " "+e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    private void interchange() {    
       //setProxy();
        loadDomainSourceOntology();
        loadSourceAndTargetFoundationalOntologies();
        loadAllMethods();
    }

    private void fileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileChooserActionPerformed

        if (sourceFileBool == false) {
            if (returnVal == fileChooser.APPROVE_OPTION) {
                domainSourceFile = fileChooser.getSelectedFile();
                newText = "Input file: " + domainSourceFile+"\n";

                if (count == 0) {
                    inputText = newText;
                    oldText = newText;
                } else {
                    inputText = oldText + "\n" + newText;
                    oldText = oldText + "\n" + newText;

                }
                loadText =inputText+"(loaded)";
                if (errorText.isEmpty()) {
                    jTextPane2.setText(loadText);

                } else {
                    jTextPane2.setText(errorText + "\n" + loadText);
                }

                jButton1.setEnabled(true);
                count++;

            } else {

            }
        }

    }//GEN-LAST:event_fileChooserActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jList1.setEnabled(false);
        jList1.update(jList1.getGraphics());
        
        jButton1.setEnabled(false);
        jButton1.update(jButton1.getGraphics());
        
        jButton2.setEnabled(false);
        jButton2.update(jButton2.getGraphics());
        interchange();
//        countDownProgressBar();

        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        input = new JLabel();
        returnVal = fileChooser.showOpenDialog(this);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
              
    //reinitialise all variables    
    count = 0;
    returnVal = 0;
    managerDomainSource = null;
    managerFoundationalTarget= null;
    managerDomainTarget = null;
    managerFoundationalSource = null;
    domainSourceFile = null;
    domainSourceOntology = null;
    mapOntology = null;
    targetFoundationalOntology = null;
    domainTargetOntology = null;
    sourceFoundationalOntology = null;
    oldText = "";
    newText = "";
    inputText = "";
    loadText = "";
    errorText = "";
    sourceFileBool = false;
    mapFileBool = false;
    mapArraySet = new HashSet();
    mapArray = new ArrayList();
    mapArrayOpSet = new HashSet();
    mapArrayOp = new ArrayList();
    factory = null;
    factorySourceBenchmark= null;
    targetIRI = null;
    sourceIRI = null;
    input = new JLabel();
    counto = 0;
    jTextPane2Text = "";
    file = null;
    fileName = ""; //filename for final onto
    tempf = "";
    tempClass = null;
    tempClassSet = null;
    tempOp = null;
    tempOpSet = null;
    interchange = false;
    ff = false;
    another = null;
    countOne = 0;
    countTwo = 0;
    mapClasses = new ArrayList();
    mapProperties = new ArrayList();
    tempSet = null;;
    tempSet2= null;;
    tempSet3= null;;
    tempSet4= null;;
    strangeSet= null;;
    strangeSet2= null;
    subBool = false;
    ancestorBool = false;    
    first = 0;
    yoho = false;
    hops = 0;
    target= null;
    target2= null;
    topclass= null;
    topop= null;
    al= null;
    al2= null;;
    date = new Date();
    time= DateFormat.getTimeInstance();
    change= null;
    sourceFO="";
    targetFO="";
    logfile= null;
    jCheckBox1.setSelected(true);
    axiomList = new ArrayList();
    badaxiomcount=0;
    goodaxiomcount=0;
    goodaxiomList = new ArrayList();
    badaxiomList = new ArrayList();
    classList = new ArrayList();
    opList = new ArrayList();
    dpList = new ArrayList();
    inList = new ArrayList();
    sourceclassList = new ArrayList();
    sourceopList = new ArrayList();
    sourcedpList = new ArrayList();
    sourceinList = new ArrayList();
    
    //reset graphics
      jButton1.setEnabled(true);
        jButton2.setEnabled(true);
        jList1.setEnabled(true);
        jList1.clearSelection();
        jProgressBar1.setValue(0);
        jTextPane2.setText("");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextPane jTextPane2;
    // End of variables declaration//GEN-END:variables
}
