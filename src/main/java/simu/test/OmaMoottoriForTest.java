package simu.test;

import simu.eduni.distributions.Normal;
import simu.framework.Kello;
import simu.framework.Tapahtuma;
import simu.model.Asiakas;
import simu.model.TapahtumanTyyppi;

// Luodaan OmaMoottoriForTest jotta ei tarvita GUI:ta testaukseen
/**
 * OmaMoottoriForTest on MoottoriForTest-luokan aliluokka, joka sisaltaa
 * palvelupisteet ja saapumisprosessin. OmaMoottoriForTest-luokkaa kaytetaan
 * testaukseen, jotta ei tarvita GUI:ta.
 *
 * @see MoottoriForTest
 * @see PalvelupisteForTest
 * @see SaapumisprosessiForTest
 */
public class OmaMoottoriForTest extends MoottoriForTest {
	/** Saapumisprosessi */
	private SaapumisprosessiForTest saapumisprosessi;
	/** Palvelupisteet */
	private PalvelupisteForTest[] palvelupisteet;

	/**
	 * Omamoottorin konstruktori, joka luo palvelupisteet ja saapumisprosessin.
	 *
	 * @see PalvelupisteForTest
	 * @see SaapumisprosessiForTest
	 */
	public OmaMoottoriForTest() {

		palvelupisteet = new PalvelupisteForTest[5];

		// Lahtoselvitys
		palvelupisteet[0] = new PalvelupisteForTest(new Normal(5, 3), tapahtumalista, TapahtumanTyyppi.DEP1);
		// Turvatarkastus
		palvelupisteet[1] = new PalvelupisteForTest(new Normal(5, 3), tapahtumalista, TapahtumanTyyppi.DEP2);
		// Passintarkistus
		palvelupisteet[2] = new PalvelupisteForTest(new Normal(5, 3), tapahtumalista, TapahtumanTyyppi.DEP3);
		// Lahtoportti kotimaanlennot
		palvelupisteet[3] = new PalvelupisteForTest(new Normal(5, 3), tapahtumalista, TapahtumanTyyppi.DEP4);
		// Lahtoportti ulkomaanlennot
		palvelupisteet[4] = new PalvelupisteForTest(new Normal(5, 3), tapahtumalista, TapahtumanTyyppi.DEP5);
		// Saapumisprosessi
		saapumisprosessi = new SaapumisprosessiForTest(tapahtumalista, TapahtumanTyyppi.ULKO, 60,
				5);
	}

	/**
	 * Palauttaa palvelupisteet.
	 *
	 * @return palvelupisteet
	 */
	public PalvelupisteForTest[] getPalvelupisteet() {
		return palvelupisteet;
	}

	/**
	 * Alustaa sapumisprosessi.
	 *{@inheritDoc}
	 * @see SaapumisprosessiForTest
	 */
	@Override
	protected void alustukset() {
		saapumisprosessi.generoiSeuraava(); // Asetetaan ensimmainen saapuminen jarjestelmaan
	}

	/**
	 * Suorittaa tapahtuman.
	 *{@inheritDoc}
	 * @param t tapahtuma
	 */
	@Override
	protected void suoritaTapahtuma(Tapahtuma t) {
		AsiakasForTest a;
		switch ((TapahtumanTyyppi) t.getTyyppi()) {
			case ARR1:
				a = new AsiakasForTest(TapahtumanTyyppi.ARR1);
				a.setUlkomaanlento();
				palvelupisteet[0].lisaaJonoon(a);
				break;
			case ARR2:
				a = new AsiakasForTest(TapahtumanTyyppi.ARR2);
				palvelupisteet[0].lisaaJonoon(a);
				break;
			case DEP1:
				a = (AsiakasForTest) palvelupisteet[0].otaJonosta();
				palvelupisteet[1].lisaaJonoon(a);
				break;
			case DEP2:
				a = (AsiakasForTest) palvelupisteet[1].otaJonosta();
				if (a.isUlkomaanlento()) {
					palvelupisteet[2].lisaaJonoon(a);

				} else {
					palvelupisteet[3].lisaaJonoon(a);

				}
				break;
			case DEP3:
				a = (AsiakasForTest) palvelupisteet[2].otaJonosta();
				palvelupisteet[4].lisaaJonoon(a);
				break;
			case DEP4:
				a = (AsiakasForTest) palvelupisteet[3].otaJonosta();
				a.setPoistumisaika(Kello.getInstance().getAika());
				Asiakas.lennolleEhtineet++;
				Asiakas.i++;
				break;
			case DEP5:
				a = (AsiakasForTest) palvelupisteet[4].otaJonosta();
				a.setPoistumisaika(Kello.getInstance().getAika());
				Asiakas.lennolleEhtineet++;
				Asiakas.i++;
				break;
			case ULKO:
				// Poistetaan jonoista kaikki ARR1-asiakkaat
				for (PalvelupisteForTest palvelupiste : palvelupisteet) {
					palvelupiste.removeAsiakasARR1();
				}
				// Poistetaan tapahtumalistan "Ulkomaalentojen"-tapahtuma
				tapahtumalista.removeUlkomaanTapahtumat();
				break;
			case SISA:
				// Poistetaan jonoista kaikki ARR2-asiakkaat
				for (PalvelupisteForTest palvelupiste : palvelupisteet) {
					palvelupiste.removeAsiakasARR2();
					palvelupiste.eiVarattu();
				}
				// Poistetaan tapahtumalistan "Sisalentojen"-tapahtuma
				tapahtumalista.removeKotimaanTapahtumat();
				break;
		}
	}


	/**
	 * Yrittaa aloittaa C tapahtumat.
	 * {@inheritDoc}
	 */
	@Override
	protected void yritaCTapahtumat() {
		for (PalvelupisteForTest p : palvelupisteet) {
			if (!p.onVarattu() && p.onJonossa()) {
				p.aloitaPalvelu();
			}
		}
	}
}