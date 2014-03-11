package crohnsdiethelper.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import de.derivo.sparqldlapi.Query;
import de.derivo.sparqldlapi.QueryArgument;
import de.derivo.sparqldlapi.QueryBinding;
import de.derivo.sparqldlapi.QueryEngine;
import de.derivo.sparqldlapi.QueryResult;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;

public class Ontology {
    private OWLOntology owl_ont;
    private OWLOntologyManager owl_manager;
    private OWLOntology owl_ontology;
    private OWLReasoner owl_reasoner;

    public Ontology(OWLOntology ontology, OWLOntologyManager manager, OWLReasoner reasoner) {
        super();
        this.owl_ontology = ontology;
        this.owl_manager = manager;
        this.owl_reasoner = reasoner;
        
        this.owl_reasoner.precomputeInferences();
    }
    
    public void TestQueries() throws QueryParserException, QueryEngineException {
        OWLDataFactory fac = this.owl_manager.getOWLDataFactory();
        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
//        Query q = Query.create(
//        "PREFIX root: <http://webprotege.stanford.edu/project/DietOntologyForCrohns#>" 
//        +" SELECT DISTINCT ?c ?label"
//        +" WHERE { Class(?c) ,"
//        +" WHERE { DirectSubClassOf(?c, <http://webprotege.stanford.edu/Rtwc1QJZQWyDwpvPSJJUr8>) }"
//        +" WHERE { DirectSubClassOf(?c, <http://webprotege.stanford.edu/R9rxq8NkMfZ4e8AhKZEPLTo>) }"
//        http://webprotege.stanford.edu/project/DietOntologyForCrohns#label
//        +" DirectSubClassOf(<http://www.w3.org/2002/07/owl#Nothing>, ?c), "
//        +" Property( <http://webprotege.stanford.edu/project/DietOntologyForCrohns#label>) }"
//        +" Annotation(?c, rdfs:label, ?label) }"
//        );
//        QueryResult qr = qengine.execute(q);
//        System.out.println("Query Results");
//        System.out.println(qr);
//        System.out.println(qr.size());
//        
        System.out.println("QueryBinding Start");
        
//        Iterator it = qr.iterator();
//        while( it.hasNext() ) {
//            QueryBinding b = (QueryBinding)it.next();
//            Set<QueryArgument> bset = b.getBoundArgs();
//            System.out.println(bset);
//            Iterator bset_it = bset.iterator();
//            while(bset_it.hasNext()) {
//                QueryArgument qa = (QueryArgument) bset_it.next();
//                System.out.println(qa);
//                System.out.println(""+ qa.getType() +", "+ qa.getValue());
//                System.out.println(b.get(qa));
//            }
//            System.out.println(b.get( QueryArgument.newVar("label")));
//        }
        
        System.out.println( this.getLeafLabels() );
        
        System.out.println("QueryBinding End");
        
        System.out.println("End Query Results");
    }
    
    public ArrayList<String> getLeafLabels() throws QueryParserException, QueryEngineException {
        ArrayList<String> out = new ArrayList<String>();
        OWLDataFactory fac = this.owl_manager.getOWLDataFactory();
        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
        Query q = Query.create(
        "PREFIX root: <http://webprotege.stanford.edu/project/DietOntologyForCrohns#>" 
        +" SELECT DISTINCT ?c ?label"
        +" WHERE { Class(?c) ,"
        +" DirectSubClassOf(<http://www.w3.org/2002/07/owl#Nothing>, ?c), "
        +" Annotation(?c, rdfs:label, ?label) }"
        );
        
        QueryResult qr = qengine.execute(q);
        System.out.println("Query Results");
        System.out.println(qr);
        System.out.println(qr.size());
        
        System.out.println("QueryBinding Start");
        
        Iterator<QueryBinding> it = qr.iterator();
        while( it.hasNext() ) {
            QueryBinding b = (QueryBinding)it.next();
//            Set<QueryArgument> bset = b.getBoundArgs();
//            System.out.println(bset);
//            Iterator bset_it = bset.iterator();
//            while(bset_it.hasNext()) {
//                QueryArgument qa = (QueryArgument) bset_it.next();
//                System.out.println(qa);
//                System.out.println(""+ qa.getType() +", "+ qa.getValue());
//                System.out.println(b.get(qa));
//            }
//            System.out.println(b.get( QueryArgument.newVar("label")));
            out.add( b.get( QueryArgument.newVar("label")).toString() );
        }
        
        return out;
    }

}
