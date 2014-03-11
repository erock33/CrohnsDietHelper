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
    OWLOntologyManager owl_manager;
    OWLOntology owl_ontology;
    Ontology ontology;
    OWLReasoner owl_reasoner;
    
    
    public OntologyBuilder() {
        super();
        this.owl_manager = OWLManager.createOWLOntologyManager();
        
    }

    /*
     * Check if there is a local ontology file. Otherwise use the
     * default provided in the jar file.
     */
    public Ontology importOWLOntology() throws OWLOntologyCreationException, QueryParserException, QueryEngineException {
        // Find OWL file
        String filename = System.getProperty("user.dir") + File.separator + "crohnsdiethelper.owl";
        File f = new File(filename);
        System.out.println(filename);
        if( f.exists() ) {
            System.out.println("Found owl file");
        }
        else {
            filename =  "data" + File.separator + "crohnsdiethelper.owl";
            System.out.println(filename);
            f = new File(filename);
            
            System.out.println("Use default");

            if( f.exists() ){
                System.out.println("Found default");
                this.owl_ontology = this.owl_manager.loadOntologyFromOntologyDocument(f);
//                this.ontology = new Ontology(owl_ontology);
                IRI documentIRI = this.owl_manager.getOntologyDocumentIRI(this.owl_ontology);
                System.out.println("    from: " + documentIRI);
            }
            else 
            {
                System.out.println("No default");
                return(null);
            }    
        }
        
        // Setup reasoner
        this.setupReasoner();
//        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
//        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
//        OWLReasonerConfiguration config = new SimpleConfiguration(
//                progressMonitor);
//        OWLReasoner reasoner = reasonerFactory.createReasoner(this.owl_ontology, config);
        
        this.ontology = new Ontology(this.owl_ontology, this.owl_manager, this.owl_reasoner);
        
        this.owl_reasoner.precomputeInferences();
        boolean consistent = this.owl_reasoner.isConsistent();
        System.out.println("Consistent: " + consistent);
        System.out.println("\n");
        
        
        
        return this.ontology;
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
        // TODO Auto-generated method stub
        return this.ontology;
    }
}