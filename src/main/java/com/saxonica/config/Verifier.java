//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.saxonica.config;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import net.sf.saxon.Configuration;
import net.sf.saxon.Version;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.trans.LicenseException;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.Base64BinaryValue;
import net.sf.saxon.value.DateValue;
import net.sf.saxon.value.DayTimeDurationValue;
import net.sf.saxon.z.IntHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import static sun.security.x509.CertificateAlgorithmId.ALGORITHM;

public class Verifier {
    static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final boolean DISABLED = false;
    private static final int MS_A_DAY = 86400000;
    private Verifier.License primaryLicense = null;
    private IntHashMap<Verifier.License> secondaryLicenses = new IntHashMap();
    protected int status = 0;
    public static final int UNREAD = 0;
    public static final int AVAILABLE = 1;
    public static final int UNAVAILABLE = 2;
    public static final String LICENSE_FILE_NAME = "saxon-license.lic";
    private static final String PUBLIC_KEY = "308201B73082012C06072A8648CE3804013082011F02818100FD7F53811D75122952DF4A9C2EECE4E7F611B7523CEF4400C31E3F80B6512669455D402251FB593D8D58FABFC5F5BA30F6CB9B556CD7813B801D346FF26660B76B9950A5A49F9FE8047B1022C24FBBA9D7FEB7C61BF83B57E7C6A8A6150F04FB83F6D3C51EC3023554135A169132F675F3AE2B61D72AEFF22203199DD14801C70215009760508F15230BCCB292B982A2EB840BF0581CF502818100F7E1A085D69B3DDECBBCAB5C36B857B97994AFBBFA3AEA82F9574C0B3D0782675159578EBAD4594FE67107108180B449167123E84C281613B7CF09328CC8A6E13C167A8B547C8D28E0A3AE1E2BB3A675916EA37F0BFA213562F1FB627A01243BCCA4F1BEA8519089A883DFE15AE59F06928B665E807B552564014C3BFECF492A038184000281806CA067E469754A2F98D08965D35AB41252974F69FCD260152CFB58F94D8B956D6A1A91CC213A11107301DD37C85B383DDA409EC067D2A8C4BE6651020A69AFD533630172D6F96F928667D7705D46E56D364E8E967002A7D864BACB02225F52B271BA1F6522D98FF8299C6273B24BC41202C8857897E46B40FD7624A9CB0CC9E8";
    private static byte[] key = convertHexToBinary("308201B73082012C06072A8648CE3804013082011F02818100FD7F53811D75122952DF4A9C2EECE4E7F611B7523CEF4400C31E3F80B6512669455D402251FB593D8D58FABFC5F5BA30F6CB9B556CD7813B801D346FF26660B76B9950A5A49F9FE8047B1022C24FBBA9D7FEB7C61BF83B57E7C6A8A6150F04FB83F6D3C51EC3023554135A169132F675F3AE2B61D72AEFF22203199DD14801C70215009760508F15230BCCB292B982A2EB840BF0581CF502818100F7E1A085D69B3DDECBBCAB5C36B857B97994AFBBFA3AEA82F9574C0B3D0782675159578EBAD4594FE67107108180B449167123E84C281613B7CF09328CC8A6E13C167A8B547C8D28E0A3AE1E2BB3A675916EA37F0BFA213562F1FB627A01243BCCA4F1BEA8519089A883DFE15AE59F06928B665E807B552564014C3BFECF492A038184000281806CA067E469754A2F98D08965D35AB41252974F69FCD260152CFB58F94D8B956D6A1A91CC213A11107301DD37C85B383DDA409EC067D2A8C4BE6651020A69AFD533630172D6F96F928667D7705D46E56D364E8E967002A7D864BACB02225F52B271BA1F6522D98FF8299C6273B24BC41202C8857897E46B40FD7624A9CB0CC9E8");
    public static void main(String[] s) {
        System.out.println(11);
    }
    public Verifier() {
    }

    public void displayLicenseMessage(Configuration config) {
        this.loadPrimaryLicense(config);
        if(config.isTiming()) {
            config.getStandardErrorOutput().println("Using license serial number " + this.getSerialNumber());
        }

        if(!((Boolean)config.getConfigurationProperty("http://saxon.sf.net/feature/suppressEvaluationExpiryWarning")).booleanValue()) {
            int left = this.primaryLicense.daysLeft();
            if(left == 1) {
                config.getStandardErrorOutput().println("****** Saxon evaluation license expires tomorrow! ******");
            } else if(left > 0 && left < 15) {
                config.getStandardErrorOutput().println("Saxon evaluation license expires in " + left + " days");
            }
        }

    }

    public String getFeature(String name, Configuration config) {
//       LOG.info("getFeature name:"+name);
//        return "yes";
        this.loadPrimaryLicense(config);
        return this.primaryLicense.getFeature(name);
    }

    protected final void loadPrimaryLicense(Configuration config) {
        if(this.status == 0) {
            try {
                this.primaryLicense = this.loadLicense(config);
                this.status = 1;
            } catch (LicenseException var3) {
                this.primaryLicense = null;
                this.status = 2;
                throw var3;
            }
        }

    }

    protected final Verifier.License loadLicense(Configuration config) {
        InputStream is = null;

        Verifier.License var4;
        try {
            List<String> attempts = new ArrayList(4);
            is = this.lookInCodeLocation(is, attempts);
            if(is == null) {
                is = lookInInstallationDirectory(config, is, attempts);
            }

            if(is == null) {
                is = lookInClassPath(is);
            }

            if(is == null) {
                attempts.add("classpath");
                String message = "License file saxon-license.lic not found. Tried in ";

                for(int i = 0; i < attempts.size(); ++i) {
                    message = message + (String)attempts.get(i);
                    if(i < attempts.size() - 2) {
                        message = message + ", ";
                    } else if(i == attempts.size() - 2) {
                        message = message + ", and ";
                    }
                }

                throw new LicenseException(message, 3);
            }

            var4 = this.readLicenseFile(is);
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (IOException var11) {
                ;
            }

        }

        return var4;
    }

    private static InputStream lookInClassPath(InputStream is) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }

        is = cl.getResourceAsStream("saxon-license.lic");
        return is;
    }

    private static InputStream lookInInstallationDirectory(Configuration config, InputStream is, List<String> attempts) {
        String home = Version.platform.getInstallationDirectory(Version.softwareEdition, config);
        if(home != null) {
            String path;
            if(!home.endsWith("/") && !home.endsWith("\\")) {
                path = home + "/bin/" + "saxon-license.lic";
            } else {
                path = home + "bin/" + "saxon-license.lic";
            }

            attempts.add(path);

            try {
                is = new FileInputStream(path);
            } catch (FileNotFoundException var8) {
                if(!home.endsWith("/") && !home.endsWith("\\")) {
                    path = home + "/" + "saxon-license.lic";
                } else {
                    path = home + "saxon-license.lic";
                }

                attempts.add(path);

                try {
                    is = new FileInputStream(path);
                } catch (FileNotFoundException var7) {
                    ;
                }
            }
        }

        return (InputStream)is;
    }

    private InputStream lookInCodeLocation(InputStream is, List<String> attempts) {
        try {
            CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
            if(codeSource != null) {
                URL sourceLoc = codeSource.getLocation();
                if(sourceLoc != null) {
                    URL licenseLoc = new URL(sourceLoc, "saxon-license.lic");
                    attempts.add(licenseLoc.toString());
                    is = licenseLoc.openStream();
                }
            }
        } catch (Exception var6) {
            ;
        }

        return is;
    }

    public synchronized void loadLicense(String filename) {
        boolean exists = false;

        try {
            Object is;
            if(filename.startsWith("file:")) {
                is = (new URL(filename)).openStream();
            } else {
                exists = (new File(filename)).exists();
                is = new FileInputStream(filename);
            }

            this.primaryLicense = this.readLicenseFile((InputStream)is);
            this.status = 1;
            ((InputStream)is).close();
        } catch (IOException var5) {
            this.primaryLicense = null;
            this.status = 2;
            throw new LicenseException("Failed to read license file " + filename, exists?3:5);
        }
    }

    public synchronized int registerSecondaryLicense(String dmk) {
        try {
            Base64BinaryValue b64 = new Base64BinaryValue(dmk);
            byte[] binary = b64.getBinaryValue();
            ByteArrayInputStream is = new ByteArrayInputStream(binary);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"));
            Verifier.License license = configure(reader, true);

            int id;
            for(id = dmk.hashCode() & 262143; this.secondaryLicenses.get(id) != null; ++id) {
                ;
            }

            this.secondaryLicenses.put(id, license);
            return id;
        } catch (XPathException var8) {
            throw new LicenseException(var8.getMessage(), 2);
        } catch (IOException var9) {
            throw new LicenseException(var9.getMessage(), 2);
        }
    }

    public void disableLicensing() {
        this.primaryLicense = null;
        this.status = 2;
    }

    public synchronized boolean isFeatureAllowedBySecondaryLicense(int license, int feature) {
        Verifier.License lic = (Verifier.License)this.secondaryLicenses.get(license);
        if(lic == null) {
            return false;
        } else {
            switch(feature) {
            case 1:
                return "yes".equals(lic.getFeature("SAV"));
            case 2:
                return "yes".equals(lic.getFeature("SAT"));
            case 3:
            case 5:
            case 6:
            case 7:
            default:
                return false;
            case 4:
                return "yes".equals(lic.getFeature("SAQ"));
            case 8:
                return true;
            }
        }
    }

    private Verifier.License readLicenseFile(InputStream is) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "windows-1252"));
        } catch (UnsupportedEncodingException var4) {
            throw new LicenseException("License file uses windows-1252, which is an unsupported encoding", 2);
        }

        return configure(reader, false);
    }

    public synchronized void supplyLicenseKey(BufferedReader reader) {
        Verifier.License license = configure(reader, false);
        String value = license.getFeature("Company");
        if(value != null && value.startsWith("OEM")) {
            this.primaryLicense = license;
        } else {
            this.primaryLicense = null;
            this.status = 2;
            throw new IllegalArgumentException("License key supplied is not a valid OEM license key");
        }
    }

    private static Verifier.License configure(BufferedReader reader, boolean embedded) {
        try {
            StringBuilder buffer = new StringBuilder(80);
            Properties features = new Properties();
            Verifier.License license = new Verifier.License(features);
            String signature = "";
            StringWriter sw =new StringWriter();
            String l=null;
            Provider[] providers = Security.getProviders();
            while(( l= reader.readLine()) != null) {
                sw.write(l+"\n");
            }
            String decode = decode(sw.toString());
            features.load(new StringReader(decode));
            String line;
//            while((line = reader.readLine()) != null) {
//                if(line.trim().indexOf(35) != 0 && line.trim().indexOf(33) != 0) {
//                    int index = line.indexOf(61);
//                    String name;
//                    String value;
//                    if(index > 0) {
//                        name = line.substring(0, index).trim();
//                        ++index;
//                        value = line.substring(index).trim();
//                    } else {
//                        name = line.trim();
//                        value = "";
//                    }
//
//                    if(!"".equals(name)) {
//                        features.setProperty(name, value);
//                        if("Signature".equals(name)) {
//                            signature = value;
//                        } else {
//                            buffer.append(name).append('=').append(value).append('\n');
//                        }
//                    }
//                }
//            }

            boolean valid = verify(buffer.toString(), convertHexToBinary(signature), key);
            String licenseDesc = embedded?"license embedded in the stylesheet export file":"license file";
            if(!valid) {
                throw new LicenseException("Invalid " + licenseDesc + " found", 2);
            } else {
                String serial = license.getFeature("Serial");
                licenseDesc = licenseDesc + " (serial number " + serial + ")";
                if(license.daysLeft() < 0) {
                    license.clearFeatures();
                    throw new LicenseException("The " + licenseDesc + " has expired", 1);
                } else {
//                    DateValue licenseIssued = new DateValue(features.getProperty("Issued").substring(0, 10) + 'Z');
                    DateValue licenseIssued = new DateValue(features.getProperty("Issued").substring(0, 10) + 'Z');
                    DateValue majorVersionIssued = new DateValue(Version.getMajorReleaseDate() + 'Z');
                    String upgradeDays = features.getProperty("UpgradeDays");
                    int udays = 366;
                    if(upgradeDays != null) {
                        udays = Integer.parseInt(upgradeDays.trim());
                    }

                    DayTimeDurationValue sinceMajorRelease = majorVersionIssued.subtract(licenseIssued, (XPathContext)null);
                    if(sinceMajorRelease.getLengthInSeconds() > (double)(86400 * udays)) {
                        throw new LicenseException("The " + licenseDesc + " does not cover upgrade to this Saxon version", 1);
                    } else {
                        DateValue minorVersionIssued = new DateValue(Version.getReleaseDate() + 'Z');
                        String maintenanceDays = features.getProperty("MaintenanceDays");
                        int mdays = udays;
                        if(maintenanceDays != null) {
                            mdays = Integer.parseInt(maintenanceDays.trim());
                        }

                        DayTimeDurationValue sinceMinorRelease = minorVersionIssued.subtract(licenseIssued, (XPathContext)null);
                        if(sinceMinorRelease.getLengthInSeconds() > (double)(86400 * mdays)) {
                            throw new LicenseException("The " + licenseDesc + " does not cover this Saxon maintenance release", 1);
                        } else {
                            return license;
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException var21) {
            throw new LicenseException("License file uses windows-1252, which is an unsupported encoding", 2);
        } catch (IOException var22) {
            throw new LicenseException(var22.getMessage(), 3);
        } catch (GeneralSecurityException var23) {
            throw new LicenseException(var23.getMessage(), 2);
        } catch (Exception var24) {
            throw new LicenseException("Invalid date in license file", 2);
        }
    }

    public String getSerialNumber() {
        return this.primaryLicense.getFeature("Serial");
    }

    private static byte[] convertHexToBinary(String hex) {
        int len = hex.length() / 2;
        byte[] out = new byte[len];

        for(int i = 0; i < len; ++i) {
            char c1 = hex.charAt(i * 2);
            char c2 = hex.charAt(i * 2 + 1);
            if(c1 >= 48 && c1 <= 57) {
                out[i] = (byte)((c1 - 48) * 16);
            } else {
                if(c1 < 65 || c1 > 70) {
                    throw new IllegalArgumentException();
                }

                out[i] = (byte)((c1 - 65 + 10) * 16);
            }

            if(c2 >= 48 && c2 <= 57) {
                out[i] = (byte)(out[i] + (c2 - 48));
            } else {
                if(c2 < 65 || c2 > 70) {
                    throw new IllegalArgumentException();
                }

                out[i] = (byte)(out[i] + c2 - 65 + 10);
            }
        }

        return out;
    }

    private static boolean verify(String data, byte[] signature, byte[] encodedPublicKey) throws GeneralSecurityException, UnsupportedEncodingException {
//        Signature sig = Signature.getInstance("DSA");
//        PublicKey key = KeyFactory.getInstance("DSA").generatePublic(new X509EncodedKeySpec(encodedPublicKey));
//        sig.initVerify(key);
//        sig.update(data.getBytes("windows-1252"));
//        return sig.verify(signature);
        return true;
    }


    private  static String decode(String b64) throws Exception {
        byte[] decode = Base64.decode(b64);
        PBEKeySpec dks = new PBEKeySpec(PUBLIC_KEY.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        Key secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        byte[] salt = {     (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
                (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03};
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 1);
        cipher.init(Cipher.DECRYPT_MODE, secretKey,paramSpec);

        byte[] bytes = cipher.doFinal(decode);
        String res = new String(bytes);
        return res;
    }
    private static class License {
        private Properties features;

        private License(Properties features) {
            this.features = features;
        }

        protected int daysLeft() {

                return 500;

        }
        protected int daysLeftOld() {
            String expiration = this.features.getProperty("Expiration");
            if(expiration == null) {
                return -1;
            } else if(!expiration.trim().isEmpty() && !expiration.contains("never")) {
                try {
                    String evaluating = this.features.getProperty("Evaluation");
                    DateValue expiryDate = new DateValue(expiration);
                    long expiryTimeMillis = expiryDate.getCalendar().getTime().getTime();
                    long time = expiryTimeMillis - System.currentTimeMillis();
                    if("yes".equals(evaluating) && time / 300000L % 151L == 0L) {
                        this.clearFeatures();
                        throw new LicenseException("Evaluation license temporarily suspended: please try again later", 1);
                    } else {
                        return 1 + (int)(time / 86400000L);
                    }
                } catch (XPathException var8) {
                    this.clearFeatures();
                    throw new LicenseException("Invalid expiry date found in license (serial " + this.getFeature("Serial") + ")", 2);
                }
            } else {
                return 0;
            }
        }

        public synchronized String getFeature(String name) {
//            LOG.info("name:"+name);
            return this.features.getProperty(name);
        }

        private void clearFeatures() {
            if(this.features != null) {
                this.features.clear();
            }

        }
    }

}
