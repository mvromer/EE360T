package ee360t.controlflow.asmgen.playground;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;

public class App {
    public static void main( String[] args ) {
        try {
            final String testClass = "ee360t.controlflow.asmgen.playground.Playground";
            ClassReader classReader = new ClassReader( testClass );
            TraceClassVisitor classVisitor;
            boolean useAsmifier = true;

            if( useAsmifier ) {
                classVisitor = new TraceClassVisitor( null, new ASMifier(), new PrintWriter( System.out ) );
            }
            else {
                classVisitor = new TraceClassVisitor( new PrintWriter( System.out ) );
            }

            classReader.accept( classVisitor, 0 );
        }
        catch( Exception ex ) {
            ex.printStackTrace();
        }
    }
}
