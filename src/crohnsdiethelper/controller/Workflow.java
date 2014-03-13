package crohnsdiethelper.controller;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import crohnsdiethelper.model.Ontology;
import crohnsdiethelper.view.RecipeRestrictionsForm;
import crohnsdiethelper.view.RecipeResultsView;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;

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
//        this.ontology.TestQueries();
        
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
               System.out.println("userIngrSet");
               System.out.println(userIngrSet);
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
                   System.out.println("recipeIngr");
                   System.out.println(recipeIngredientIRI);
                   
                   // For each ingredient, check if it's available
                   System.out.println(userIngrSet);
                   Iterator j = recipeIngredientIRI.iterator();
                   drinkFound = true;
                   drinkIngr = new ArrayList<String>();

                   while(j.hasNext()) {
                       String ingIRI = (String) j.next();
                       System.out.println(ingIRI + " "+ userIngrSet.contains(ingIRI));
                       if( userIngrSet.contains(ingIRI) ){
                           System.out.println("Found "+ingIRI);
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
                            System.out.println(subs);   
                       }
                   }
                   
                   if( drinkFound ) {
                       break;
                   }
                   
               }
               
               if( drinkFound ){
                   System.out.println("Here's your drink");
                   System.out.println(recipeIRI);
                   System.out.println(recipeIngredientIRI);
                   System.out.println(drinkIngr);
               }
               else
               {
                   System.out.println("Could not find any recipes.");
               }
               
               try {
                   resultsView.updateView(recipeIRI, drinkIngr);
                } catch (QueryParserException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (QueryEngineException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                // Give recipe to results view
                cardlayout.show(cardpanel, RECIPE_RESULTS);
            }
        });
        
        
        this.frame.add(this.cardpanel);
    }

    
    
}
