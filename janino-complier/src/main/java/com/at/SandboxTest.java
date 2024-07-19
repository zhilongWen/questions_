package com.at;

import org.codehaus.commons.compiler.Sandbox;
import org.codehaus.janino.ScriptEvaluator;

import java.security.Permissions;
import java.security.PrivilegedAction;
import java.util.PropertyPermission;

public class SandboxTest {
    public static void
    main(String[] args) throws Exception {

        // Create a JANINO script evaluator. The example, however, will work as fine with
        // ExpressionEvaluators, ClassBodyEvaluators and SimpleCompilers.
        ScriptEvaluator se = new ScriptEvaluator();
        se.setDebuggingInformation(true, true, false);

        // Now create a "Permissions" object which allows to read the system variable
        // "foo", and forbids everything else.
        Permissions permissions = new Permissions();
        permissions.add(new PropertyPermission("foo", "read"));

        // Compile a simple script which reads two system variables - "foo" and "bar".
        PrivilegedAction<?> pa = se.createFastEvaluator((
                "System.getProperty(\"foo\");\n" +
                        "System.getProperty(\"bar\");\n" +
                        "return null;\n"
        ), PrivilegedAction.class, new String[0]);

        // Finally execute the script in the sandbox. Getting system property "foo" will
        // succeed, and getting "bar" will throw a
        //    java.security.AccessControlException: access denied (java.util.PropertyPermission bar read)
        // in line 2 of the script. Et voila!
        Sandbox sandbox = new Sandbox(permissions);
        sandbox.confine(pa);
    }
}
