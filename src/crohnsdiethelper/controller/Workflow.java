package crohnsdiethelper.controller;

import java.awt.CardLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import crohnsdiethelper.model.Ontology;
import crohnsdiethelper.view.RecipeRestrictionsForm;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;

public class Workflow {    
    private JFrame frame;
    private JPanel cards;
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
        this.cards = new JPanel( new CardLayout() );
        this.ontology_builder = new OntologyBuilder();
        this.ontology_builder.importOWLOntology();
        this.ontology = this.ontology_builder.getOntology();
        this.ontology.TestQueries();
    }
    
    public void start() {
//        this.frame.pack();
//        this.frame.setVisible(true);
    }
    
    public void buildRecipeRestrictionsForm() {
        RecipeRestrictionsForm form = new RecipeRestrictionsForm();
        
        form.buildForm();
        
    }


    
}
