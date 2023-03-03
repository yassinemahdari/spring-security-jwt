package ma.hps.powercard.compliance.serviceimpl.spec;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ma.hps.powercard.annotation.ApiMapping;
import ma.hps.powercard.dto.ErrorResponse;
import ma.hps.powercard.dto.SuccessResponse;

@RestController
@RequestMapping(value = "/compliance")
public class VersionServiceRest {

	@ApiMapping("/version")
	public String version() {
		
		try {
			return SuccessResponse.from(VersionService.getVersion()).toJson();
		} catch (IOException e) {
			return ErrorResponse.from("0000").toJson();
		}

	}

}

class VersionService {
	
	static String APPVERSION_PROPERTY_NAME = "Tag";

	static Version version = null;
	
	static Version getVersion() throws IOException {
		
		if (VersionService.version != null)
			return version;

		List<URL> res = Collections.list(Thread.currentThread().getContextClassLoader()
				.getResources("META-INF/MANIFEST.MF"));

		ModuleNameExtractor.extractAppWarName(res);
		
		Version version = new Version();

		List<ModuleUrlManifest> moduleUrlManifests = res.stream()
			.filter(Objects::nonNull)
			.filter(url -> url.toString().contains(ModuleNameExtractor.appWarName))
			.map(ModuleUrlManifest::new)
			.collect(Collectors.toList());
		
		version.extractVersions(moduleUrlManifests);
		
		VersionService.version = version;
		return version;
	}

}

class Version implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String appVersion;
	private Map<String, String> modulesVersions = new HashMap<String, String>();
	
	Version() {}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public Map<String, String> getModulesVersions() {
		return modulesVersions;
	}

	public void setModulesVersions(Map<String, String> modulesVersions) {
		this.modulesVersions = modulesVersions;
	}
	
	public void extractVersions(Collection<ModuleUrlManifest> mums) {
		mums.stream()
		  .forEach(mum -> {
			  if (mum.getUrl().toString().contains(ModuleNameExtractor.moduleSnitch)) {
				  // this means that we are in one of the modules
				  this.modulesVersions.put(mum.getModule(), mum.getVersion());
			  } else {
				  // this is our app version
				  this.appVersion = mum.getVersion();
			  }
		  });
	}

}

class ModuleUrlManifest {
	
	private URL url;
	private String module;
	private Manifest manifest;

	public ModuleUrlManifest(URL url) {
		this.url = url;
		this.module = ModuleNameExtractor.extractModuleName(url);
		try {
			this.manifest = new Manifest(url.openStream());
		} catch (IOException e) {
			this.manifest = null;
		}
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Manifest getManifest() {
		return manifest;
	}

	public void setManifest(Manifest manifest) {
		this.manifest = manifest;
	}
	
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getVersion() {
		return this.manifest != null ? this.getManifest().getMainAttributes().getValue(VersionService.APPVERSION_PROPERTY_NAME) : null;
	}
	
}

class ModuleNameExtractor {
	
	public  static String appWarName;
	
	private static final String appSnitch    = ".war/META-INF/MANIFEST.MF";
	public static String moduleSnitch = "/WEB-INF/lib/";

	public static String extractModuleName(URL url) {
		
		// To be called after `extractAppWarName(Collection<URL> urls)`.

		final String before = "/v3_31-web.war/WEB-INF/lib/";
		final String after  = "-";
		
		// working on a URL that resembles.
		// "vfs:/content/v3_31-web.war/WEB-INF/lib/paramsCardholder-3.5.1.jar/META-INF/MANIFEST.MF"
		//
		// we take what's after "/v3_31-web.war/WEB-INF/lib/" and that's: 
		//   "paramsCardholder-3.5.1.jar/META-INF/MANIFEST.MF"
		//
		// and we take everything until the first '-' character.
		// and that should be: "paramsCardholder"

		if (url == null) {
			return "";
		}

		return getAfterAndBefore(url.toString(), before, after);
	}

	public static void extractAppWarName(Collection<URL> urls) {
		
		if (isNotEmpty(appWarName))
			return;
		
		final String before = "/content/";
		final String after  = "/META-INF/";

		// working on a URL that resembles.
		// "vfs:/content/v3_31-web.war/META-INF/MANIFEST.MF"
		//
		// we take what's after "/content/" and that's: 
		//   "v3_31-web.war/META-INF/MANIFEST.MF"
		//
		// and we take everything until the first "/META-INF" string.
		// and that should be: "v3_31-web.war"

		appWarName = urls.stream()
			.map(URL::toString)
			.filter(url -> url.contains(appSnitch))
			.findFirst()
			.map(url -> getAfterAndBefore(url, before, after))
			.orElse("");
		
		moduleSnitch = appWarName + moduleSnitch;
	}
	
	private static String getAfterAndBefore(String haystach, String before, String after) {
		
		if (isEmpty(haystach) || isEmpty(before) || isEmpty(after))
			return null;

		final int beginIndex = haystach.indexOf(before) + before.length();
		final int endIndex   = haystach.substring(beginIndex).indexOf(after) + beginIndex;

		return haystach.substring(beginIndex, endIndex);
	}

}