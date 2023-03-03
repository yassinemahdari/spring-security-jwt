package ma.hps.powercard.compliance.specificserviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import org.fornax.cartridges.sculptor.framework.errorhandling.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ma.hps.powercard.compliance.serviceapi.Diners_binVO;
import ma.hps.powercard.compliance.serviceapi.Pcrd_flex_diners_binsService;
import ma.hps.powercard.compliance.serviceapi.Generate_diners_binsOutVO;
import ma.hps.powercard.compliance.serviceapi.Generate_diners_binsInVO;


@Component
public class DinersBinSepcificServiceImpl {

	@Autowired
	private Pcrd_flex_diners_binsService pcrd_flex_diners_binsService;



	public Generate_diners_binsOutVO generateSpecficBinsService(ServiceContext ctx, Diners_binVO dinersBinVoIn)
			throws Exception {
		try {

			Generate_diners_binsOutVO generate_diners_binsOutVO = new Generate_diners_binsOutVO();
			Generate_diners_binsInVO diners_binsInVO = new Generate_diners_binsInVO();
			diners_binsInVO.setP_abrv_wording(dinersBinVoIn.getAbrv_wording());
			diners_binsInVO.setP_wording(dinersBinVoIn.getWording());
			diners_binsInVO.setP_low_issuer_bin(dinersBinVoIn.getBin_low().toString());
			diners_binsInVO.setP_max_issuer_bin(dinersBinVoIn.getBin_high().toString());
			generate_diners_binsOutVO = pcrd_flex_diners_binsService.generate_diners_bins(ctx,diners_binsInVO);
			return generate_diners_binsOutVO;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



}