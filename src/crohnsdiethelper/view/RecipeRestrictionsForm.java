package crohnsdiethelper.view;

import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import crohnsdiethelper.model.Ontology;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;

public class RecipeRestrictionsForm {
    JPanel panel;
    
    public JPanel buildForm(Ontology ontology) throws QueryParserException, QueryEngineException {
        this.panel = new JPanel();
        
        ArrayList<String> foods = ontology.getLeafLabels();

        String[] arr = foods.toArray(new String[ foods.size() ]);
        JList mylist = new JList(arr);
        mylist.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        
        JScrollPane spane = new JScrollPane(mylist);
//        JScrollBar mylistscroll = new JScrollBar( JScrollBar.VERTICAL );
        
        this.panel.add(spane);
        
        String[] colors = {"Green", "Orange", "Purple", "Red"};
        JComboBox juicecolors = new JComboBox(colors);
        this.panel.add(juicecolors);
        
        
        
        return this.panel;
    }
}
