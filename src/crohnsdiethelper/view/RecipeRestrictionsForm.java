package crohnsdiethelper.view;

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
    
    public JPanel buildForm(Ontology ontology) throws QueryParserException, QueryEngineException {
        this.panel = new JPanel();
        this.foodCheckboxes = new ArrayList<JCheckBox>();
        
        ArrayList<String> foods = ontology.getLeafLabels();
        ArrayList<String> colors = ontology.getColors();
        
        Collections.sort(foods);
        Collections.sort(colors);
        
        String[] arr = foods.toArray(new String[ foods.size() ]);
        String[] colors_ar = colors.toArray(new String[colors.size()] );
        JList mylist = new JList(arr);
        mylist.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
                
        JPanel checkpanel = new JPanel(new GridLayout(foods.size(),2,5,5));
        checkpanel.setAutoscrolls(true);
        
        for( Iterator<String> i = foods.iterator(); i.hasNext(); ) {
            String item = i.next();
            JCheckBox box = new JCheckBox(item);
            this.foodCheckboxes.add(box);
            checkpanel.add(box);
        }
        
        JScrollPane spane = new JScrollPane(checkpanel);
        spane.setPreferredSize(new Dimension( 200, 800) );
        
        
        this.panel.add(spane);
        
        this.juicecolors = new JComboBox(colors_ar);
        this.panel.add(this.juicecolors);
        
        this.findbtn = new JButton("Find");
        this.panel.add(this.findbtn);
           
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
