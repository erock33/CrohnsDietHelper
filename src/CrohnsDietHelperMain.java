import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import crohnsdiethelper.controller.Workflow;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;

/*
 * This class just holds the main method and
 * is responsible for performing the bare 
 * minimum to get the program up and running.
 * 
 */
public class CrohnsDietHelperMain {
    
    public static void main(String[] args) throws OWLOntologyCreationException, QueryParserException, QueryEngineException {
        Workflow workflow = new Workflow();
        workflow.configure();
        workflow.start();
    }

}
