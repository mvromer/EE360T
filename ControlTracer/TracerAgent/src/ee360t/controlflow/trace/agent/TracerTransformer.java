package ee360t.controlflow.trace.agent;

import org.objectweb.asm.ClassReader;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class TracerTransformer implements ClassFileTransformer {
    private List<String> prefixesToTrace;

    public TracerTransformer( List<String> prefixesToTrace ) {
        this.prefixesToTrace = new ArrayList<>( prefixesToTrace.size() );
        for( String prefix : prefixesToTrace ) {
            this.prefixesToTrace.add( prefix.replace( '.', '/' ) );
        }
    }

    @Override
    public byte[] transform( ClassLoader loader, String className, Class<?> classBeingRedefined,
                             ProtectionDomain protectionDomain, byte[] classfileBuffer )
        throws IllegalClassFormatException {
        ClassReader classReader = new ClassReader( classfileBuffer );
        System.out.println( "Transforming: " + className );
        return null;
    }
}
