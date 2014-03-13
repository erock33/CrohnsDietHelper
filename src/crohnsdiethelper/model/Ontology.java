package crohnsdiethelper.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
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
    private IRI iri;

    public Ontology(OWLOntology ontology, OWLOntologyManager manager, OWLReasoner reasoner, IRI iri) {
        super();
        this.owl_ontology = ontology;
        this.owl_manager = manager;
        this.owl_reasoner = reasoner;
        this.iri = iri;
        
        this.owl_reasoner.precomputeInferences();
    }
    
    public ArrayList<String> getLeafLabels() throws QueryParserException, QueryEngineException {
        ArrayList<String> out = new ArrayList<String>();
        this.owl_reasoner.precomputeInferences();
        OWLDataFactory fac = this.owl_manager.getOWLDataFactory();

        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
        Query q = Query.create(
        "PREFIX root: <http://protege.stanford.edu/bmi210/DOC_Ontology_Hobbs_Taylor#>" 
        +" SELECT DISTINCT ?label "
        +" WHERE { "
        +" Class(?c), "
        +" DirectSubClassOf(<http://www.w3.org/2002/07/owl#Nothing>, ?c),"
        +" Type(?i, ?c),"
        +" Annotation(?c, rdfs:label, ?label)"
        +" }"
        );
        
        QueryResult qr = qengine.execute(q);
        
        Iterator<QueryBinding> it = qr.iterator();
        while( it.hasNext() ) {
            QueryBinding b = (QueryBinding)it.next();
            Set<QueryArgument> bset = b.getBoundArgs();
            Iterator bset_it = bset.iterator();
            while(bset_it.hasNext()) {
                QueryArgument qa = (QueryArgument) bset_it.next();

                OWLNamedIndividual ind = fac.getOWLNamedIndividual(IRI.create(b.get(qa).toString()));
            }

            String label = b.get( QueryArgument.newVar("label")).toString();
            label = label.replaceAll("\"","");
            out.add(label);
        }
        
        return out;
    }
    
    public ArrayList<String> getColors() throws QueryParserException, QueryEngineException {
        ArrayList<String> out = new ArrayList<String>();
        OWLDataFactory fac = this.owl_manager.getOWLDataFactory();
        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
        Query q = Query.create(
        "PREFIX root: <http://protege.stanford.edu/bmi210/DOC_Ontology_Hobbs_Taylor#>" 
        +" SELECT DISTINCT ?color "
        +" WHERE { "
        + "Individual(?i), "
        +" PropertyValue(?i, root:isJuiceColor, ?color) "
        +" }"
        );
        
        QueryResult qr = qengine.execute(q);
        
        Iterator<QueryBinding> it = qr.iterator();
        while( it.hasNext() ) {
            QueryBinding b = (QueryBinding)it.next();
            String color = b.get( QueryArgument.newVar("color")).toString();
            color = color.replaceAll("\"", "");
            out.add( color );
        }
        
        return out;
    }
    
    public ArrayList<String> getRecipeIRIs(String color) throws QueryParserException, QueryEngineException {
        ArrayList<String> out = new ArrayList<String>();
        
        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
        String query = 
        "PREFIX root: <http://protege.stanford.edu/bmi210/DOC_Ontology_Hobbs_Taylor#>" 
        +" SELECT DISTINCT ?i "
        +" WHERE { "
            +" Individual(?i),"
            +" PropertyValue(?i, root:hasRecipe, \"true\"),"
            +" PropertyValue(?i, root:isJuiceColor, \""+color+ "\")"
        +" }";
        Query q = Query.create(query);
        
        QueryResult qr = qengine.execute(q);

        Iterator<QueryBinding> it = qr.iterator();
        while( it.hasNext() ) {
            QueryBinding b = (QueryBinding)it.next();
            Set<QueryArgument> bset = b.getBoundArgs();
            Iterator bset_it = bset.iterator();
            while(bset_it.hasNext()) {
                QueryArgument qa = (QueryArgument) bset_it.next();

                out.add(b.get(qa).toString());
            }
        }

        return out;
    }
        
    public ArrayList<String> recipeIngredientIRIs(String recipeIRI) throws QueryParserException, QueryEngineException {
        ArrayList<String> ingredients = new ArrayList<String>();
        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
        String prefix = "http://protege.stanford.edu/bmi210/DOC_Ontology_Hobbs_Taylor#";
        String query = 
        "PREFIX root: <"+ prefix +">"
        +" SELECT DISTINCT ?ingredient "
        +" WHERE { "
        +" SubPropertyOf( ?ip, root:hasIngredient), "
        +" PropertyValue( <" + recipeIRI + ">, ?ip, ?ingredient) "
        +" }"
        ;

        Query q = Query.create(query);
        QueryResult qr = qengine.execute(q);
        
        Iterator<QueryBinding> it = qr.iterator();
        while( it.hasNext() ) {
            QueryBinding b = (QueryBinding)it.next();
            Set<QueryArgument> bset = b.getBoundArgs();
            Iterator bset_it = bset.iterator();
            while(bset_it.hasNext()) {
                QueryArgument qa = (QueryArgument) bset_it.next();

                ingredients.add(b.get(qa).toString());
            }
        }

        return ingredients;
    }

    public ArrayList<String> getIngredientInstances(String ingrClass) throws QueryParserException, QueryEngineException {
        ArrayList<String> ingredients = new ArrayList<String>();

        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
        String prefix = "http://protege.stanford.edu/bmi210/DOC_Ontology_Hobbs_Taylor#";
        String query = 
        "PREFIX root: <"+ prefix +">"
        +" SELECT DISTINCT ?inst "
        +" WHERE { "
        + "Type( ?inst, <"+ ingrClass +"> )"
        +" }"
        ;
        Query q = Query.create(query);
        QueryResult qr = qengine.execute(q);
        
        Iterator<QueryBinding> it = qr.iterator();
        while( it.hasNext() ) {
            QueryBinding b = (QueryBinding)it.next();
            Set<QueryArgument> bset = b.getBoundArgs();
            Iterator bset_it = bset.iterator();
            while(bset_it.hasNext()) {
                QueryArgument qa = (QueryArgument) bset_it.next();

                ingredients.add(b.get(qa).toString());
            }
        }

        return ingredients;
    }
    
    public ArrayList<String> getIngredientSubstitutes(String ingrIRI) throws QueryEngineException, QueryParserException {
        ArrayList<String> out = new ArrayList<String>();
        
        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
        String prefix = "http://protege.stanford.edu/bmi210/DOC_Ontology_Hobbs_Taylor#";
        String query = 
        "PREFIX root: <"+ prefix +">"
        +" SELECT DISTINCT ?ingredient "
        +" WHERE { "
        +" SubPropertyOf( ?subprop, root:hasSubstitute), "
        +" PropertyValue( <" + ingrIRI + ">, ?subprop, ?ingredient) "
        +" }"
        ;
        Query q = Query.create(query);
        QueryResult qr = qengine.execute(q);
        
        Iterator<QueryBinding> it = qr.iterator();
        while( it.hasNext() ) {
            QueryBinding b = (QueryBinding)it.next();
            Set<QueryArgument> bset = b.getBoundArgs();
            Iterator bset_it = bset.iterator();
            while(bset_it.hasNext()) {
                QueryArgument qa = (QueryArgument) bset_it.next();

                out.add(b.get(qa).toString());
            }
        }
        
        return out;
    }
    
    public String getRecipeName(String recipeIRI) throws QueryParserException, QueryEngineException {
        String ret = "";
        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
        String prefix = "http://protege.stanford.edu/bmi210/DOC_Ontology_Hobbs_Taylor#";
        String query = 
        "PREFIX root: <"+ prefix +">"
        +" SELECT DISTINCT ?name "
        +" WHERE { "
        +" PropertyValue( <" + recipeIRI + ">, root:hasRecipeNameJuice, ?name) "
        +" }"
        +" OR WHERE {"
        +" PropertyValue( <" + recipeIRI + ">, root:hasRecipeNameSmoothie, ?name)"
        + "}"
        ;
        Query q = Query.create(query);
        QueryResult qr = qengine.execute(q);
                
        Iterator<QueryBinding> it = qr.iterator();
        while( it.hasNext() ) {
            QueryBinding b = (QueryBinding)it.next();
            Set<QueryArgument> bset = b.getBoundArgs();
            Iterator bset_it = bset.iterator();
            while(bset_it.hasNext()) {
                QueryArgument qa = (QueryArgument) bset_it.next();

                ret = b.get(qa).toString();
                break;
            }
            break;
        }
        
        return ret;
    }
    
    public String getIngredientLabel( String ingrIRI ) throws QueryParserException, QueryEngineException {
        String label = "";
        
        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
        String prefix = "http://protege.stanford.edu/bmi210/DOC_Ontology_Hobbs_Taylor#";
        String query = 
        "PREFIX root: <"+ prefix +">"
        +" SELECT DISTINCT ?label "
        +" WHERE { "
        +" Type( <"+ingrIRI+">, ?c),"
        +" DirectSubClassOf(<http://www.w3.org/2002/07/owl#Nothing>, ?c),"
        +" Annotation(?c, rdfs:label, ?label)"
        + "}"
        ;
        Query q = Query.create(query);
        QueryResult qr = qengine.execute(q);
                
        Iterator<QueryBinding> it = qr.iterator();
        while( it.hasNext() ) {
            QueryBinding b = (QueryBinding)it.next();

            label = b.get( QueryArgument.newVar("label")).toString();
            label = label.replaceAll("\"","");
        }
        
        return label;
    }
    
    public String getIngredientSize( String ingrIRI ) throws QueryParserException, QueryEngineException {
        String size = "";
        
        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
        String prefix = "http://protege.stanford.edu/bmi210/DOC_Ontology_Hobbs_Taylor#";
        String query = 
        "PREFIX root: <"+ prefix +">"
        +" SELECT DISTINCT ?size "
        +" WHERE { "
        +" PropertyValue( <"+ingrIRI+">, root:hasSize, ?size)"
        + "}"
        ;
        Query q = Query.create(query);
        QueryResult qr = qengine.execute(q);
                
        Iterator<QueryBinding> it = qr.iterator();
        while( it.hasNext() ) {
            QueryBinding b = (QueryBinding)it.next();

            size = b.get( QueryArgument.newVar("size")).toString();
            size = size.replaceAll("\"","");
        }
        
        return size;
    }
    
    public String getIngredientQuantity( String ingrIRI ) throws QueryParserException, QueryEngineException {
        String quant = "";
        
        QueryEngine qengine = QueryEngine.create(this.owl_manager, this.owl_reasoner);
        String prefix = "http://protege.stanford.edu/bmi210/DOC_Ontology_Hobbs_Taylor#";
        String query = 
        "PREFIX root: <"+ prefix +">"
        +" SELECT DISTINCT ?quant "
        +" WHERE { "
        +" PropertyValue( <"+ingrIRI+">, root:hasQuantity, ?quant)"
        + "}"
        ;
        
        Query q = Query.create(query);
        QueryResult qr = qengine.execute(q);
                
        Iterator<QueryBinding> it = qr.iterator();
        while( it.hasNext() ) {
            QueryBinding b = (QueryBinding)it.next();

            quant = b.get( QueryArgument.newVar("quant")).toString();
            quant = quant.replaceAll("\"","");
        }
        
        return quant;
    }
}
