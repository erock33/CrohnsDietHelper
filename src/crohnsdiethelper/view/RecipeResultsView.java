package crohnsdiethelper.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import crohnsdiethelper.model.Ontology;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;

public class RecipeResultsView {
    JPanel panel;
    JPanel ingrList;
    Ontology ontology;
    
    public JPanel buildView(Ontology ontology) {
        this.ontology = ontology;
        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout(0, 0));
        
        
        return this.panel;
    }

    public void updateView(String recipeIRI, ArrayList<String> drinkIngr) throws QueryParserException, QueryEngineException {
        String name = ontology.getRecipeName(recipeIRI);
        System.out.println(name);
        JLabel title = new JLabel(name);
        Font titleFont = title.getFont();
        title.setFont(new Font(titleFont.getName(), Font.PLAIN, 20));
        this.panel.add(title, BorderLayout.NORTH);
        
        String[] listVals = new String[drinkIngr.size()];
        
        Iterator i = drinkIngr.iterator();
        Integer j = 0;
        while(i.hasNext()) {
            String ingrIRI = (String)i.next();
            String label = ontology.getIngredientLabel(ingrIRI);
            String size = ontology.getIngredientSize(ingrIRI);
            String quant = ontology.getIngredientQuantity(ingrIRI);
            
            String listVal = label +": "+ quant +" "+ size;
            listVals[j] = listVal;

            j = j + 1;
        }
        
        this.panel.add(new JList(listVals));
    }
    
    
}
