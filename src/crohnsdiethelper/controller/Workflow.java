package crohnsdiethelper.controller;

import java.awt.CardLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import crohnsdiethelper.model.Ontology;
import crohnsdiethelper.view.RecipeRestrictionsForm;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;

public class Workflow {    
    private JFrame frame;
    private CardLayout cardlayout;
    private JPanel cardpanel;
    private Ontology ontology;
    private OntologyBuilder ontology_builder;
    
    final static String RECIPE_RESTRICTIONS_FORM = "RecipeRestrictionsForm";
    final static String RECIPE_RESULTS = "RecipeResults";
    
    public Workflow() {
        super();
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public Workflow( JFrame f ) {
        super();
        this.frame = f;
    }
    
    public void configure() throws OWLOntologyCreationException, QueryParserException, QueryEngineException {
        this.cardlayout = new CardLayout();
        this.cardpanel = new JPanel( this.cardlayout );
        this.ontology_builder = new OntologyBuilder();
        this.ontology_builder.importOWLOntology();
        this.ontology = this.ontology_builder.getOntology();
        this.ontology.TestQueries();
        
        this.buildRecipeRestrictionsForm();
    }
    
    public void start() {
        this.frame.setSize(300,400);
        this.frame.pack();
        this.frame.setVisible(true);
    }
    
    public void buildRecipeRestrictionsForm() throws QueryParserException, QueryEngineException {
        RecipeRestrictionsForm form = new RecipeRestrictionsForm();

        this.cardpanel.add( form.buildForm(this.ontology), RECIPE_RESTRICTIONS_FORM );
        this.frame.add(this.cardpanel);
    }


    
}
