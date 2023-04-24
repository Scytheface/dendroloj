package ee.ut.dendroloj;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachineManager;
import com.sun.tools.attach.AttachNotSupportedException;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.VirtualMachine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

class DebuggerTracer {

    static void init() {
        boolean inDebugger = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().stream().anyMatch(s -> s.contains("jdwp"));
        System.out.println(System.getProperties());
        long pid = ProcessHandle.current().pid();
        String path = "C:\\Users\\admin\\.jdks\\openjdk-19\\bin\\jdwp.dll";
        ProcessBuilder processBuilder = new ProcessBuilder("jcmd", String.valueOf(pid), "JVMTI.agent_load " + path + "=transport=dt_socket,server=n,suspend=y,address=localhost:49522");
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("wat = " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
