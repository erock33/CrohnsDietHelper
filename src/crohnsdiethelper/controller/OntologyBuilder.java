package crohnsdiethelper.controller;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import crohnsdiethelper.model.Ontology;
import de.derivo.sparqldlapi.Query;
import de.derivo.sparqldlapi.QueryArgument;
import de.derivo.sparqldlapi.QueryBinding;
import de.derivo.sparqldlapi.QueryEngine;
import de.derivo.sparqldlapi.QueryResult;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;
import de.derivo.sparqldlapi.types.QueryArgumentType;

public class OntologyBuilder {
    private OWLOntologyManager owl_manager;
    private OWLOntology owl_ontology;
    private Ontology ontology;
    private OWLReasoner owl_reasoner;
    private IRI iri;
    
    public OntologyBuilder() {
        super();
        this.owl_manager = OWLManager.createOWLOntologyManager();
        
    }

    /*
     * Check if there is a local ontology file. Otherwise use the
     * default provided in the jar file.
     */
    public OWLOntology importOWLOntology() throws OWLOntologyCreationException, QueryParserException, QueryEngineException {
        // Find OWL file
        String filename = System.getProperty("user.dir") + File.separator + "crohnsdiethelper.owl";
        File f = new File(filename);
        if( f.exists() ) {
            // Found owl file, we should use it instead
        }
        else {
            filename =  "data" + File.separator + "crohnsdiethelper.owl";
            f = new File(filename);

            if( f.exists() ){
                this.owl_ontology = this.owl_manager.loadOntologyFromOntologyDocument(f);
                this.iri = this.owl_manager.getOntologyDocumentIRI(this.owl_ontology);
            }
            else 
            {
                return(null);
            }    
        }
                
        // Setup reasoner
        this.setupReasoner();
        
        this.ontology = new Ontology(this.owl_ontology, this.owl_manager, this.owl_reasoner, this.iri);
        
        this.owl_reasoner.precomputeInferences();
        boolean consistent = this.owl_reasoner.isConsistent();
               
        return this.owl_ontology;
    }
    
    public OWLReasoner setupReasoner() {
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        OWLReasonerConfiguration config = new SimpleConfiguration(
                progressMonitor);
        this.owl_reasoner = reasonerFactory.createReasoner(this.owl_ontology, config);
        
        return this.owl_reasoner;
    }

    public Ontology getOntology() {
        return this.ontology;
    }
}
