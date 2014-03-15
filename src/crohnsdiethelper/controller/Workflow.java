package crohnsdiethelper.controller;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import crohnsdiethelper.model.Ontology;
import crohnsdiethelper.view.RecipeRestrictionsForm;
import crohnsdiethelper.view.RecipeResultsView;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;

/*
 * Workflow class
 * 
 * This class is responsible for coordinating between forms
 */
public class Workflow {    
    private JFrame frame;
    private CardLayout cardlayout;
    private JPanel cardpanel;
    private Ontology ontology;
    private OntologyBuilder ontology_builder;
    private RecipeRestrictionsForm form;
    private RecipeResultsView resultsView;
    
    final static String RECIPE_RESTRICTIONS_FORM = "RecipeRestrictionsForm";
    final static String RECIPE_RESULTS = "RecipeResults";
    
    public Workflow() {
        super();
        this.frame = new JFrame("Crohns Diet Helper");
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
        
        this.buildRecipeRestrictionsForm();
    }
    
    public void start() {
        this.frame.setSize(400,800);
        this.frame.pack();
        this.frame.setVisible(true);
    }
    
    public void buildRecipeRestrictionsForm() throws QueryParserException, QueryEngineException {
        this.form = new RecipeRestrictionsForm();
        this.resultsView = new RecipeResultsView();
        
        this.cardpanel.add( form.buildForm(this.ontology), RECIPE_RESTRICTIONS_FORM );
        this.cardpanel.add( resultsView.buildView(this.ontology), RECIPE_RESULTS);
        
        resultsView.getBackButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardlayout.show(cardpanel, RECIPE_RESTRICTIONS_FORM);
            }
        });
        
        // Here we have the code to find the 
        form.getFindButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                // Get list of ingredients
               ArrayList<JCheckBox> ingredientList = form.getCheckBoxes();
               ArrayList<String> userIngredients = new ArrayList<String>();
               String prefix = "http://protege.stanford.edu/bmi210/DOC_Ontology_Hobbs_Taylor#";

               
               Iterator i = ingredientList.iterator();
               while( i.hasNext() ) {
                   JCheckBox box = (JCheckBox) i.next();
                   if( box.isSelected() ) {
                       ArrayList<String> ingrInst = new ArrayList<String>();
                       try {
                        ingrInst = ontology.getIngredientInstances(prefix + box.getText());
                        } catch (QueryParserException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (QueryEngineException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                       userIngredients.addAll(ingrInst);
                   }
               }
               HashSet<String> userIngrSet = new HashSet<String>(userIngredients);

               // Stick ingredient list in hash for quick retrieval later
               
               // Get Color
               String color = form.getSelectedColor();
                
                // Find recipe
               ArrayList<String> recipeIRIs = new ArrayList<String>();
               try {
                    recipeIRIs = ontology.getRecipeIRIs(color);
                } catch (QueryParserException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (QueryEngineException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
               i = recipeIRIs.iterator();
               boolean drinkFound = false;
               ArrayList<String> drinkIngr = new ArrayList<String>();
               String recipeIRI = "";
               ArrayList<String> recipeIngredientIRI = new ArrayList<String>();
               
               while( i.hasNext() || drinkFound ) {
                   recipeIRI = (String)i.next();
                   // Get ingredient list for recipe
                   recipeIngredientIRI = new ArrayList<String>();
                   try {
                    recipeIngredientIRI = ontology.recipeIngredientIRIs(recipeIRI);
                    } catch (QueryParserException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (QueryEngineException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                   // For each ingredient, check if it's available
                   Iterator j = recipeIngredientIRI.iterator();
                   drinkFound = true;
                   drinkIngr = new ArrayList<String>();

                   while(j.hasNext()) {
                       String ingIRI = (String) j.next();
                       if( userIngrSet.contains(ingIRI) ){
                           drinkIngr.add(ingIRI);
                       }
                       else
                       {
                           // Try and find a substitute
                           ArrayList<String> subs = new ArrayList<String>();
                           try {
                            subs = ontology.getIngredientSubstitutes(ingIRI);
                            } catch (QueryEngineException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            } catch (QueryParserException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                           if( subs.size() == 0 ) {
                               // There were no substitutes available so 
                               // we will move on to another recipe.
                               drinkFound = false;
                               break;
                           }
                           
                           // For now, we only will ever need one of the substitutes.
                           // This may change in the future if we keep track of how much
                           // of each item the user has.
                           drinkIngr.add(subs.get(0));
                       }
                   }
                   
                   if( drinkFound ) {
                       break;
                   }
                   
               }
               
               if( drinkFound ){
                   try {
                       resultsView.updateView(recipeIRI, drinkIngr);
                    } catch (QueryParserException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (QueryEngineException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
               }
               else
               {
                   resultsView.updateView();
               }

                // Give recipe to results view
                cardlayout.show(cardpanel, RECIPE_RESULTS);
            }
        });
        
        
        this.frame.add(this.cardpanel);
    }  
}
