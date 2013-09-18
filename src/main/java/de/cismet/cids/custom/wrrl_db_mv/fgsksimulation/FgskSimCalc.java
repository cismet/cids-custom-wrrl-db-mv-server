/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.wrrl_db_mv.fgsksimulation;

import org.apache.log4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class FgskSimCalc {

    //~ Static fields/initializers ---------------------------------------------

    private static Logger LOG = Logger.getLogger(FgskSimCalc.class);
    private static FgskSimCalc instance;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FgskSimCalc object.
     */
    private FgskSimCalc() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public synchronized FgskSimCalc getInstance() {
        if (instance == null) {
            instance = new FgskSimCalc();
        }

        return instance;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kaBean     DOCUMENT ME!
     * @param   simMaBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public double calcCosts(final CidsBean kaBean, final CidsBean simMaBean) throws Exception {
        try {
            final ScriptEngineManager manager = new ScriptEngineManager();
            final ScriptEngine engine = manager.getEngineByName("js");
            String costFormula = (String)simMaBean.getProperty("kosten");
            String calculationRule = (String)simMaBean.getProperty("kostenformel");

            if (costFormula != null) {
                costFormula = replaceVariables(costFormula, kaBean);
                final Object costs = engine.eval(costFormula);

                if (costs == null) {
                    LOG.warn("Costs are null");
                }

                calculationRule = calculationRule.replaceAll("KOSTEN", String.valueOf(costs));
            }

            calculationRule = replaceVariables(calculationRule, kaBean);

            final Object costs = engine.eval(calculationRule);

            if (costs instanceof Number) {
                return ((Number)costs).doubleValue();
            } else {
                final String message = "illegal cost settings: " + calculationRule; // NOI18N
                LOG.error(message);
                throw new IllegalStateException(message);
            }
        } catch (final Exception e) {
            LOG.error("Error while calculating costs", e);
            throw e;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   formula  DOCUMENT ME!
     * @param   kaBean   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String replaceVariables(final String formula, final CidsBean kaBean) {
        final String breadth = (String)kaBean.getProperty("gewaesserbreite_id.name");
        final Integer wbType = (Integer)kaBean.getProperty("gewaessertyp_id.value");
        final Double sohlsubstrKuenst = (Double)kaBean.getProperty("sohlensubstrat_kue");

        String newFormula = formula.replaceAll("LAENGE", String.valueOf(getKaLength(kaBean)));
        newFormula = newFormula.replaceAll("BREITE", String.valueOf(breadth));
        newFormula = newFormula.replaceAll("TYP", String.valueOf(wbType));
        newFormula = newFormula.replaceAll("SUBSTRAT", String.valueOf(sohlsubstrKuenst));

        return newFormula;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kaBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static double getKaLength(final CidsBean kaBean) {
        try {
            final Double toValue = (Double)kaBean.getProperty("linie.bis.wert");
            final Double fromValue = (Double)kaBean.getProperty("linie.von.wert");

            return Math.abs(toValue - fromValue);
        } catch (final Exception e) {
            final String message = "illegal station settings in kartierabschnitt"; // NOI18N
            LOG.error(message, e);
            throw new IllegalStateException(message, e);
        }
    }
}
