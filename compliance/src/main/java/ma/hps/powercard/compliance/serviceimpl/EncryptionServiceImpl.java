package ma.hps.powercard.compliance.serviceimpl;

import java.security.MessageDigest;

import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
/**
 * Implementation of EncryptionService.
 */
@Lazy
@Service("encryptionService")
public class EncryptionServiceImpl extends EncryptionServiceImplBase {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public EncryptionServiceImpl() {
    }

    public String encryptPassword(ServiceContext ctx, String password,String salt)
        throws Exception {
        
        return passwordEncoder.encode(password);
    	/*String  passWithSalt = password+"{"+salt+"}" ;    	
    	MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
    	byte[] Hash =sha512.digest(passWithSalt.getBytes());
    	return buffer_to_hex(Hash);*/

    }
    
    private String buffer_to_hex(byte[] hash)
    {
        String d = "";
        for (int i = 0; i < hash.length; i++)
        {
            int v = hash[i] & 0xFF;
            if(v < 16)
                d += "0";
            d += Integer.toString(v, 16) + "";
        }
        return d;
    }
}
