package cz.cuni.mff.ufal.utils;

/**
 * @author Petr Fanta
 */
public class SystemInfo {

    public enum JVMArch {
        x86,
        x64,
        unknown
    }

    private static final String[] PROPERTY_KEYS = {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

    public static JVMArch getJVMArch() {
        for (String key : PROPERTY_KEYS) {
            String value = System.getProperty(key);
            if (value != null) {
                if (value.contains("64")) {
                    return JVMArch.x64;
                } else {
                    return JVMArch.x86;
                }
            }
        }

        return JVMArch.unknown;
    }
}
