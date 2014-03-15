package crohnsdiethelper.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import crohnsdiethelper.model.Ontology;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;

public class RecipeRestrictionsForm {
    JPanel panel;
    JButton findbtn;
    ArrayList<JCheckBox> foodCheckboxes;
    JComboBox juicecolors;
    
    public Component buildForm(Ontology ontology) throws QueryParserException, QueryEngineException {
        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout(10, 10));
        this.foodCheckboxes = new ArrayList<JCheckBox>();
        
        ArrayList<String> foods = ontology.getLeafLabels();
        ArrayList<String> colors = ontology.getColors();
        
        Collections.sort(foods);
        Collections.sort(colors);
        
        String[] colors_ar = colors.toArray(new String[colors.size()] );
                
        JPanel checkpanel = new JPanel(new GridLayout(foods.size(),2,5,5));
        checkpanel.setAutoscrolls(true);
        
        for( Iterator<String> i = foods.iterator(); i.hasNext(); ) {
            String item = i.next();
            JCheckBox box = new JCheckBox(item);
            this.foodCheckboxes.add(box);
            checkpanel.add(box);
        }
        
        JScrollPane spane = new JScrollPane(checkpanel, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spane.setPreferredSize(new Dimension( 300, 400) );
        
        this.panel.add(spane, BorderLayout.CENTER);
        
        this.juicecolors = new JComboBox(colors_ar);
        this.panel.add(this.juicecolors, BorderLayout.EAST);
        
        this.findbtn = new JButton("Find");
        this.panel.add(this.findbtn, BorderLayout.NORTH);
           
        return this.panel;
    }
    
    public JButton getFindButton() {
        return this.findbtn;
    }
    
    public ArrayList<JCheckBox> getCheckBoxes() {
        return this.foodCheckboxes;
    }
    
    public String getSelectedColor() {
        return (String)this.juicecolors.getSelectedItem();
    }
    
    public void addButtonListener( ActionListener listener ) {
        this.findbtn.addActionListener(listener);
    }
    
}
