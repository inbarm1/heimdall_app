package com.example.inbar.heimdall;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends APIRequest {
    public static final String EFFICIENCY   = "party_efficiency";
    public static final String PROPOSALS    = "num_of_proposals";
    public static final String MISSING      = "party_missing";
    public static final String EFFICIENCY_M = "memeber_efficiency";
    public static final String PROPOSALS_M  = "elected_proposals";
    public static final String MISSING_M    = "member_missing";
    public static final String EFFICIENCY_T = "יעילות המפלגות";
    public static final String PROPOSALS_T  = "הצעות חוק";
    public static final String MISSING_T    = "העדרויות";
    public static final String TITLE_PIE    = "התפלגות";
    Map<String, JSONObject> chart1 = new HashMap<>();
    Map<String, JSONObject> chart2 = new HashMap<>();
    Map<String, JSONObject> chart3 = new HashMap<>();

    private CoordinatorLayout mainLayout;
    PieChart mChart;
    String jsonTest1 = "{\n" +
            "    \"אינם חברי כנסת\": {\n" +
            "        \"member_missing\": {},\n" +
            "        \"party_missing\": 0\n" +
            "    },\n" +
            "    \"הבית היהודי\": {\n" +
            "        \"member_missing\": {\n" +
            "            \"אורי יהודה אריאל\": 0.12919463087248323,\n" +
            "            \"איילת שקד\": 0.16275167785234898,\n" +
            "            \"אלי בן-דהן\": 0.1040268456375839,\n" +
            "            \"בצלאל סמוטריץ\": 0.10738255033557047,\n" +
            "            \"מרדכי יוגב\": 0.11912751677852348,\n" +
            "            \"ניסן סלומינסקי\": 0.1040268456375839,\n" +
            "            \"נפתלי בנט\": 0.1644295302013423,\n" +
            "            \"שולי מועלם-רפאלי\": 0.10906040268456375\n" +
            "        },\n" +
            "        \"party_missing\": 0.745\n" +
            "    },\n" +
            "    \"הליכוד\": {\n" +
            "        \"member_missing\": {\n" +
            "            \"אבי (משה) דיכטר\": 0.036989247311827955,\n" +
            "            \"אברהם נגוסה\": 0.022365591397849462,\n" +
            "            \"אופיר אקוניס\": 0.04,\n" +
            "            \"אורן אסף חזן\": 0.036989247311827955,\n" +
            "            \"איוב קרא\": 0.03139784946236559,\n" +
            "            \"אמיר אוחנה\": 0.02924731182795699,\n" +
            "            \"בנימין נתניהו\": 0.04258064516129032,\n" +
            "            \"גילה גמליאל\": 0.03913978494623656,\n" +
            "            \"גלעד ארדן\": 0.038279569892473116,\n" +
            "            \"דוד אמסלם\": 0.03612903225806452,\n" +
            "            \"דוד ביטן\": 0.025376344086021504,\n" +
            "            \"ז`קי לוי\": 0.03483870967741935,\n" +
            "            \"זאב אלקין\": 0.03870967741935484,\n" +
            "            \"זאב בנימין בגין\": 0.030107526881720432,\n" +
            "            \"חיים כץ\": 0.03655913978494624,\n" +
            "            \"יהודה יהושע גליק\": 0.036989247311827955,\n" +
            "            \"יואב קיש\": 0.03225806451612903,\n" +
            "            \"יובל שטייניץ\": 0.035698924731182795,\n" +
            "            \"יולי יואל אדלשטיין\": 0.036989247311827955,\n" +
            "            \"ירון מזוז\": 0.034408602150537634,\n" +
            "            \"יריב לוין\": 0.03096774193548387,\n" +
            "            \"ישראל כץ\": 0.0378494623655914,\n" +
            "            \"מירי רגב\": 0.03741935483870968,\n" +
            "            \"מכלוף מיקי זוהר\": 0.02967741935483871,\n" +
            "            \"נאוה בוקר\": 0.025806451612903226,\n" +
            "            \"נורית קורן\": 0.022795698924731184,\n" +
            "            \"ענת ברקו\": 0.026236559139784947,\n" +
            "            \"צחי הנגבי\": 0.034408602150537634,\n" +
            "            \"ציפי חוטובלי\": 0.030107526881720432,\n" +
            "            \"שרן השכל\": 0.02967741935483871\n" +
            "        },\n" +
            "        \"party_missing\": 0.775\n" +
            "    },\n" +
            "    \"המחנה הציוני\": {\n" +
            "        \"member_missing\": {\n" +
            "            \"איילת נחמיאס ורבין\": 0.046218487394957986,\n" +
            "            \"איל בן-ראובן\": 0.03721488595438175,\n" +
            "            \"איציק שמולי\": 0.022809123649459785,\n" +
            "            \"איתן ברושי\": 0.04441776710684274,\n" +
            "            \"איתן כבל\": 0.042617046818727494,\n" +
            "            \"זוהיר בהלול\": 0.040216086434573826,\n" +
            "            \"יואל חסון\": 0.03721488595438175,\n" +
            "            \"יוסי יונה\": 0.043817527010804325,\n" +
            "            \"יחיאל חיליק בר\": 0.02460984393757503,\n" +
            "            \"יעל כהן פארן\": 0.030612244897959183,\n" +
            "            \"יצחק הרצוג\": 0.0546218487394958,\n" +
            "            \"לאה פדידה\": 0.05102040816326531,\n" +
            "            \"מיכל בירן\": 0.047418967587034816,\n" +
            "            \"מיקי רוזנטל\": 0.036614645858343335,\n" +
            "            \"מרב מיכאלי\": 0.03721488595438175,\n" +
            "            \"נחמן שי\": 0.028211284513805522,\n" +
            "            \"סאלח סעד\": 0.05402160864345738,\n" +
            "            \"סתיו שפיר\": 0.030612244897959183,\n" +
            "            \"עמיר פרץ\": 0.058823529411764705,\n" +
            "            \"עמר בר-לב\": 0.047418967587034816,\n" +
            "            \"ציפי לבני\": 0.05102040816326531,\n" +
            "            \"קסניה סבטלובה\": 0.046218487394957986,\n" +
            "            \"רויטל סויד\": 0.05042016806722689,\n" +
            "            \"שלי יחימוביץ`\": 0.036614645858343335\n" +
            "        },\n" +
            "        \"party_missing\": 0.6941666666666667\n" +
            "    },\n" +
            "    \"הרשימה המשותפת\": {\n" +
            "        \"member_missing\": {\n" +
            "            \"אחמד טיבי\": 0.08084163898117387,\n" +
            "            \"איימן עודה\": 0.06201550387596899,\n" +
            "            \"ג'מעה אזברגה\": 0.06423034330011074,\n" +
            "            \"ג`מאל זחאלקה\": 0.09745293466223699,\n" +
            "            \"דב חנין\": 0.044296788482835,\n" +
            "            \"חנין זועבי\": 0.09413067552602436,\n" +
            "            \"טלב אבו עראר\": 0.0753045404208195,\n" +
            "            \"יוסף ג`בארין\": 0.07751937984496124,\n" +
            "            \"יוסף עטאונה\": 0.10188261351052048,\n" +
            "            \"מסעוד גנאים\": 0.08637873754152824,\n" +
            "            \"סעיד אלחרומי\": 0.09191583610188261,\n" +
            "            \"עאידה תומא סלימאן\": 0.07641196013289037,\n" +
            "            \"עבד אלחכים חאג` יחיא\": 0.047619047619047616\n" +
            "        },\n" +
            "        \"party_missing\": 0.6946153846153846\n" +
            "    },\n" +
            "    \"ח”כ יחיד – אורלי לוי-אבקסיס\": {\n" +
            "        \"member_missing\": {\n" +
            "            \"אורלי לוי-אבקסיס\": 1\n" +
            "        },\n" +
            "        \"party_missing\": 0.84\n" +
            "    },\n" +
            "    \"יהדות התורה\": {\n" +
            "        \"member_missing\": {\n" +
            "            \"אורי מקלב\": 0.14425427872860636,\n" +
            "            \"יעקב אשר\": 0.1491442542787286,\n" +
            "            \"יעקב ליצמן\": 0.2078239608801956,\n" +
            "            \"ישראל אייכלר\": 0.1784841075794621,\n" +
            "            \"מנחם אליעזר מוזס\": 0.15403422982885084,\n" +
            "            \"משה גפני\": 0.16625916870415647\n" +
            "        },\n" +
            "        \"party_missing\": 0.6816666666666666\n" +
            "    },\n" +
            "    \"יש עתיד\": {\n" +
            "        \"member_missing\": {\n" +
            "            \"אלעזר שטרן\": 0.10167310167310167,\n" +
            "            \"חיים ילין\": 0.08365508365508366,\n" +
            "            \"יאיר לפיד\": 0.11840411840411841,\n" +
            "            \"יואל רזבוזוב\": 0.0978120978120978,\n" +
            "            \"יעל גרמן\": 0.06435006435006435,\n" +
            "            \"יעקב פרי\": 0.08751608751608751,\n" +
            "            \"מאיר כהן\": 0.07335907335907337,\n" +
            "            \"מיקי לוי\": 0.055341055341055344,\n" +
            "            \"עליזה לביא\": 0.09395109395109395,\n" +
            "            \"עפר שלח\": 0.10810810810810811,\n" +
            "            \"קארין אלהרר\": 0.11583011583011583\n" +
            "        },\n" +
            "        \"party_missing\": 0.7063636363636364\n" +
            "    },\n" +
            "    \"ישראל ביתנו\": {\n" +
            "        \"member_missing\": {\n" +
            "            \"חמד עמאר\": 0.16216216216216217,\n" +
            "            \"יוליה מלינובסקי\": 0.19594594594594594,\n" +
            "            \"סופה לנדבר\": 0.2195945945945946,\n" +
            "            \"עודד פורר\": 0.20945945945945946,\n" +
            "            \"רוברט אילטוב\": 0.21283783783783783\n" +
            "        },\n" +
            "        \"party_missing\": 0.592\n" +
            "    },\n" +
            "    \"כולנו\": {\n" +
            "        \"member_missing\": {\n" +
            "            \"אכרם חסון\": 0.06304985337243402,\n" +
            "            \"אלי אלאלוף\": 0.093841642228739,\n" +
            "            \"אלי כהן\": 0.13196480938416422,\n" +
            "            \"טלי פלוסקוב\": 0.0747800586510264,\n" +
            "            \"יואב גלנט\": 0.13049853372434017,\n" +
            "            \"יפעת שאשא ביטון\": 0.11143695014662756,\n" +
            "            \"מיכאל אורן\": 0.10997067448680352,\n" +
            "            \"מירב בן-ארי\": 0.10997067448680352,\n" +
            "            \"רועי פולקמן\": 0.0718475073313783,\n" +
            "            \"רחל עזריה\": 0.10263929618768329\n" +
            "        },\n" +
            "        \"party_missing\": 0.682\n" +
            "    },\n" +
            "    \"מרצ\": {\n" +
            "        \"member_missing\": {\n" +
            "            \"אילן גילאון\": 0.18429003021148035,\n" +
            "            \"מיכל רוזין\": 0.17220543806646527,\n" +
            "            \"משה (מוסי) רז\": 0.25075528700906347,\n" +
            "            \"עיסאווי פריג`\": 0.18731117824773413,\n" +
            "            \"תמר זנדברג\": 0.2054380664652568\n" +
            "        },\n" +
            "        \"party_missing\": 0.662\n" +
            "    },\n" +
            "    \"ש”ס\": {\n" +
            "        \"member_missing\": {\n" +
            "            \"דוד אזולאי\": 0.158004158004158,\n" +
            "            \"יואב בן-צור\": 0.13305613305613306,\n" +
            "            \"יעקב מרגי\": 0.1496881496881497,\n" +
            "            \"יצחק וקנין\": 0.11850311850311851,\n" +
            "            \"יצחק כהן\": 0.14553014553014554,\n" +
            "            \"מיכאל מלכיאלי\": 0.10395010395010396,\n" +
            "            \"משולם נהרי\": 0.19126819126819128\n" +
            "        },\n" +
            "        \"party_missing\": 0.6871428571428572\n" +
            "    }\n" +
            "}";
    String jsonTest2 = "{\n" +
            "    \"הבית היהודי\": {\n" +
            "        \"elected_proposals\": {\n" +
            "            \"מרדכי יוגב\": 0.25,\n" +
            "            \"ניסן סלומינסקי\": 0.25,\n" +
            "            \"שולי מועלם-רפאלי\": 0.5\n" +
            "        },\n" +
            "        \"num_of_proposals\": 0.08\n" +
            "    },\n" +
            "    \"הליכוד\": {\n" +
            "        \"elected_proposals\": {\n" +
            "            \"אברהם נגוסה\": 0.06060606060606061,\n" +
            "            \"אורן אסף חזן\": 0.030303030303030304,\n" +
            "            \"דוד אמסלם\": 0.09090909090909091,\n" +
            "            \"דוד ביטן\": 0.24242424242424243,\n" +
            "            \"יואב קיש\": 0.15151515151515152,\n" +
            "            \"מכלוף מיקי זוהר\": 0.06060606060606061,\n" +
            "            \"נאוה בוקר\": 0.12121212121212122,\n" +
            "            \"נורית קורן\": 0.09090909090909091,\n" +
            "            \"ענת ברקו\": 0.09090909090909091,\n" +
            "            \"שרן השכל\": 0.06060606060606061\n" +
            "        },\n" +
            "        \"num_of_proposals\": 0.33\n" +
            "    },\n" +
            "    \"המחנה הציוני\": {\n" +
            "        \"elected_proposals\": {\n" +
            "            \"איילת נחמיאס ורבין\": 0.034482758620689655,\n" +
            "            \"איציק שמולי\": 0.08620689655172414,\n" +
            "            \"איתן ברושי\": 0.017241379310344827,\n" +
            "            \"איתן כבל\": 0.05172413793103448,\n" +
            "            \"יואל חסון\": 0.05172413793103448,\n" +
            "            \"יוסי יונה\": 0.1206896551724138,\n" +
            "            \"יחיאל חיליק בר\": 0.08620689655172414,\n" +
            "            \"מיכל בירן\": 0.034482758620689655,\n" +
            "            \"מיקי רוזנטל\": 0.06896551724137931,\n" +
            "            \"מרב מיכאלי\": 0.06896551724137931,\n" +
            "            \"נחמן שי\": 0.10344827586206896,\n" +
            "            \"סתיו שפיר\": 0.034482758620689655,\n" +
            "            \"עמיר פרץ\": 0.034482758620689655,\n" +
            "            \"עמר בר-לב\": 0.08620689655172414,\n" +
            "            \"קסניה סבטלובה\": 0.06896551724137931,\n" +
            "            \"רויטל סויד\": 0.05172413793103448\n" +
            "        },\n" +
            "        \"num_of_proposals\": 0.58\n" +
            "    },\n" +
            "    \"הרשימה המשותפת\": {\n" +
            "        \"elected_proposals\": {\n" +
            "            \"אחמד טיבי\": 0.10526315789473684,\n" +
            "            \"איימן עודה\": 0.10526315789473684,\n" +
            "            \"דב חנין\": 0.42105263157894735,\n" +
            "            \"חנין זועבי\": 0.10526315789473684,\n" +
            "            \"טלב אבו עראר\": 0.05263157894736842,\n" +
            "            \"מסעוד גנאים\": 0.10526315789473684,\n" +
            "            \"עאידה תומא סלימאן\": 0.10526315789473684\n" +
            "        },\n" +
            "        \"num_of_proposals\": 0.19\n" +
            "    },\n" +
            "    \"יהדות התורה\": {\n" +
            "        \"elected_proposals\": {\n" +
            "            \"אורי מקלב\": 0.45454545454545453,\n" +
            "            \"יעקב אשר\": 0.09090909090909091,\n" +
            "            \"מנחם אליעזר מוזס\": 0.09090909090909091,\n" +
            "            \"משה גפני\": 0.36363636363636365\n" +
            "        },\n" +
            "        \"num_of_proposals\": 0.11\n" +
            "    },\n" +
            "    \"יש עתיד\": {\n" +
            "        \"elected_proposals\": {\n" +
            "            \"אלעזר שטרן\": 0.09090909090909091,\n" +
            "            \"חיים ילין\": 0.13636363636363635,\n" +
            "            \"יעל גרמן\": 0.09090909090909091,\n" +
            "            \"יעקב פרי\": 0.18181818181818182,\n" +
            "            \"מאיר כהן\": 0.09090909090909091,\n" +
            "            \"מיקי לוי\": 0.09090909090909091,\n" +
            "            \"עליזה לביא\": 0.09090909090909091,\n" +
            "            \"עפר שלח\": 0.13636363636363635,\n" +
            "            \"קארין אלהרר\": 0.09090909090909091\n" +
            "        },\n" +
            "        \"num_of_proposals\": 0.22\n" +
            "    },\n" +
            "    \"ישראל ביתנו\": {\n" +
            "        \"elected_proposals\": {\n" +
            "            \"יוליה מלינובסקי\": 0.14285714285714285,\n" +
            "            \"עודד פורר\": 0.2857142857142857,\n" +
            "            \"רוברט אילטוב\": 0.5714285714285714\n" +
            "        },\n" +
            "        \"num_of_proposals\": 0.07\n" +
            "    },\n" +
            "    \"כולנו\": {\n" +
            "        \"elected_proposals\": {\n" +
            "            \"אכרם חסון\": 0.2857142857142857,\n" +
            "            \"אלי אלאלוף\": 0.07142857142857142,\n" +
            "            \"טלי פלוסקוב\": 0.14285714285714285,\n" +
            "            \"יפעת שאשא ביטון\": 0.14285714285714285,\n" +
            "            \"רועי פולקמן\": 0.2857142857142857,\n" +
            "            \"רחל עזריה\": 0.07142857142857142\n" +
            "        },\n" +
            "        \"num_of_proposals\": 0.14\n" +
            "    },\n" +
            "    \"מרצ\": {\n" +
            "        \"elected_proposals\": {\n" +
            "            \"אילן גילאון\": 0.3,\n" +
            "            \"מיכל רוזין\": 0.5,\n" +
            "            \"תמר זנדברג\": 0.2\n" +
            "        },\n" +
            "        \"num_of_proposals\": 0.1\n" +
            "    },\n" +
            "    \"ש”ס\": {\n" +
            "        \"elected_proposals\": {\n" +
            "            \"יעקב מרגי\": 0.7777777777777778,\n" +
            "            \"יצחק וקנין\": 0.1111111111111111,\n" +
            "            \"מיכאל מלכיאלי\": 0.1111111111111111\n" +
            "        },\n" +
            "        \"num_of_proposals\": 0.09\n" +
            "    }\n" +
            "}";
    String jsonTest = "{\n" +
            "    \"אינם חברי כנסת\": {\n" +
            "        \"memeber_efficiency\": {},\n" +
            "        \"party_efficiency\": 0\n" +
            "    },\n" +
            "    \"הבית היהודי\": {\n" +
            "        \"memeber_efficiency\": {\n" +
            "            \"אורי יהודה אריאל\": 0.11274509803921569,\n" +
            "            \"איילת שקד\": 0.014705882352941176,\n" +
            "            \"אלי בן-דהן\": 0.18627450980392157,\n" +
            "            \"בצלאל סמוטריץ\": 0.17647058823529413,\n" +
            "            \"מרדכי יוגב\": 0.14215686274509803,\n" +
            "            \"ניסן סלומינסקי\": 0.18627450980392157,\n" +
            "            \"נפתלי בנט\": 0.00980392156862745,\n" +
            "            \"שולי מועלם-רפאלי\": 0.1715686274509804\n" +
            "        },\n" +
            "        \"party_efficiency\": 0.255\n" +
            "    },\n" +
            "    \"הליכוד\": {\n" +
            "        \"memeber_efficiency\": {\n" +
            "            \"אבי (משה) דיכטר\": 0.02074074074074074,\n" +
            "            \"אברהם נגוסה\": 0.07111111111111111,\n" +
            "            \"אופיר אקוניס\": 0.01037037037037037,\n" +
            "            \"אורן אסף חזן\": 0.02074074074074074,\n" +
            "            \"איוב קרא\": 0.04,\n" +
            "            \"אמיר אוחנה\": 0.047407407407407405,\n" +
            "            \"בנימין נתניהו\": 0.0014814814814814814,\n" +
            "            \"גילה גמליאל\": 0.013333333333333334,\n" +
            "            \"גלעד ארדן\": 0.016296296296296295,\n" +
            "            \"דוד אמסלם\": 0.023703703703703703,\n" +
            "            \"דוד ביטן\": 0.06074074074074074,\n" +
            "            \"ז`קי לוי\": 0.028148148148148148,\n" +
            "            \"זאב אלקין\": 0.014814814814814815,\n" +
            "            \"זאב בנימין בגין\": 0.044444444444444446,\n" +
            "            \"חיים כץ\": 0.022222222222222223,\n" +
            "            \"יהודה יהושע גליק\": 0.02074074074074074,\n" +
            "            \"יואב קיש\": 0.037037037037037035,\n" +
            "            \"יובל שטייניץ\": 0.025185185185185185,\n" +
            "            \"יולי יואל אדלשטיין\": 0.02074074074074074,\n" +
            "            \"ירון מזוז\": 0.02962962962962963,\n" +
            "            \"יריב לוין\": 0.04148148148148148,\n" +
            "            \"ישראל כץ\": 0.017777777777777778,\n" +
            "            \"מירי רגב\": 0.01925925925925926,\n" +
            "            \"מכלוף מיקי זוהר\": 0.045925925925925926,\n" +
            "            \"נאוה בוקר\": 0.05925925925925926,\n" +
            "            \"נורית קורן\": 0.06962962962962962,\n" +
            "            \"ענת ברקו\": 0.057777777777777775,\n" +
            "            \"צחי הנגבי\": 0.02962962962962963,\n" +
            "            \"ציפי חוטובלי\": 0.044444444444444446,\n" +
            "            \"שרן השכל\": 0.045925925925925926\n" +
            "        },\n" +
            "        \"party_efficiency\": 0.225\n" +
            "    },\n" +
            "    \"המחנה הציוני\": {\n" +
            "        \"memeber_efficiency\": {\n" +
            "            \"איילת נחמיאס ורבין\": 0.031335149863760216,\n" +
            "            \"איל בן-ראובן\": 0.051771117166212535,\n" +
            "            \"איציק שמולי\": 0.08446866485013624,\n" +
            "            \"איתן ברושי\": 0.035422343324250684,\n" +
            "            \"איתן כבל\": 0.039509536784741145,\n" +
            "            \"זוהיר בהלול\": 0.04495912806539509,\n" +
            "            \"יואל חסון\": 0.051771117166212535,\n" +
            "            \"יוסי יונה\": 0.03678474114441417,\n" +
            "            \"יחיאל חיליק בר\": 0.08038147138964577,\n" +
            "            \"יעל כהן פארן\": 0.0667574931880109,\n" +
            "            \"יצחק הרצוג\": 0.01226158038147139,\n" +
            "            \"לאה פדידה\": 0.020435967302452316,\n" +
            "            \"מיכל בירן\": 0.02861035422343324,\n" +
            "            \"מיקי רוזנטל\": 0.05313351498637602,\n" +
            "            \"מרב מיכאלי\": 0.051771117166212535,\n" +
            "            \"נחמן שי\": 0.07220708446866485,\n" +
            "            \"סאלח סעד\": 0.013623978201634877,\n" +
            "            \"סתיו שפיר\": 0.0667574931880109,\n" +
            "            \"עמיר פרץ\": 0.0027247956403269754,\n" +
            "            \"עמר בר-לב\": 0.02861035422343324,\n" +
            "            \"ציפי לבני\": 0.020435967302452316,\n" +
            "            \"קסניה סבטלובה\": 0.031335149863760216,\n" +
            "            \"רויטל סויד\": 0.021798365122615803,\n" +
            "            \"שלי יחימוביץ`\": 0.05313351498637602\n" +
            "        },\n" +
            "        \"party_efficiency\": 0.30583333333333335\n" +
            "    },\n" +
            "    \"הרשימה המשותפת\": {\n" +
            "        \"memeber_efficiency\": {\n" +
            "            \"אחמד טיבי\": 0.06801007556675064,\n" +
            "            \"איימן עודה\": 0.11083123425692695,\n" +
            "            \"ג'מעה אזברגה\": 0.10579345088161209,\n" +
            "            \"ג`מאל זחאלקה\": 0.030226700251889168,\n" +
            "            \"דב חנין\": 0.15113350125944586,\n" +
            "            \"חנין זועבי\": 0.037783375314861464,\n" +
            "            \"טלב אבו עראר\": 0.08060453400503778,\n" +
            "            \"יוסף ג`בארין\": 0.07556675062972293,\n" +
            "            \"יוסף עטאונה\": 0.020151133501259445,\n" +
            "            \"מסעוד גנאים\": 0.055415617128463476,\n" +
            "            \"סעיד אלחרומי\": 0.042821158690176324,\n" +
            "            \"עאידה תומא סלימאן\": 0.07808564231738035,\n" +
            "            \"עבד אלחכים חאג` יחיא\": 0.14357682619647355\n" +
            "        },\n" +
            "        \"party_efficiency\": 0.30538461538461537\n" +
            "    },\n" +
            "    \"ח”כ יחיד – אורלי לוי-אבקסיס\": {\n" +
            "        \"memeber_efficiency\": {\n" +
            "            \"אורלי לוי-אבקסיס\": 1\n" +
            "        },\n" +
            "        \"party_efficiency\": 0.16\n" +
            "    },\n" +
            "    \"יהדות התורה\": {\n" +
            "        \"memeber_efficiency\": {\n" +
            "            \"אורי מקלב\": 0.21465968586387435,\n" +
            "            \"יעקב אשר\": 0.20418848167539266,\n" +
            "            \"יעקב ליצמן\": 0.07853403141361257,\n" +
            "            \"ישראל אייכלר\": 0.14136125654450263,\n" +
            "            \"מנחם אליעזר מוזס\": 0.193717277486911,\n" +
            "            \"משה גפני\": 0.16753926701570682\n" +
            "        },\n" +
            "        \"party_efficiency\": 0.31833333333333336\n" +
            "    },\n" +
            "    \"יש עתיד\": {\n" +
            "        \"memeber_efficiency\": {\n" +
            "            \"אלעזר שטרן\": 0.06501547987616099,\n" +
            "            \"חיים ילין\": 0.10835913312693499,\n" +
            "            \"יאיר לפיד\": 0.02476780185758514,\n" +
            "            \"יואל רזבוזוב\": 0.07430340557275542,\n" +
            "            \"יעל גרמן\": 0.15479876160990713,\n" +
            "            \"יעקב פרי\": 0.09907120743034056,\n" +
            "            \"מאיר כהן\": 0.13312693498452013,\n" +
            "            \"מיקי לוי\": 0.17647058823529413,\n" +
            "            \"עליזה לביא\": 0.08359133126934984,\n" +
            "            \"עפר שלח\": 0.04953560371517028,\n" +
            "            \"קארין אלהרר\": 0.030959752321981424\n" +
            "        },\n" +
            "        \"party_efficiency\": 0.29363636363636364\n" +
            "    },\n" +
            "    \"ישראל ביתנו\": {\n" +
            "        \"memeber_efficiency\": {\n" +
            "            \"חמד עמאר\": 0.2549019607843137,\n" +
            "            \"יוליה מלינובסקי\": 0.20588235294117646,\n" +
            "            \"סופה לנדבר\": 0.1715686274509804,\n" +
            "            \"עודד פורר\": 0.18627450980392157,\n" +
            "            \"רוברט אילטוב\": 0.18137254901960784\n" +
            "        },\n" +
            "        \"party_efficiency\": 0.408\n" +
            "    },\n" +
            "    \"כולנו\": {\n" +
            "        \"memeber_efficiency\": {\n" +
            "            \"אכרם חסון\": 0.1792452830188679,\n" +
            "            \"אלי אלאלוף\": 0.11320754716981132,\n" +
            "            \"אלי כהן\": 0.031446540880503145,\n" +
            "            \"טלי פלוסקוב\": 0.1540880503144654,\n" +
            "            \"יואב גלנט\": 0.03459119496855346,\n" +
            "            \"יפעת שאשא ביטון\": 0.07547169811320754,\n" +
            "            \"מיכאל אורן\": 0.07861635220125786,\n" +
            "            \"מירב בן-ארי\": 0.07861635220125786,\n" +
            "            \"רועי פולקמן\": 0.16037735849056603,\n" +
            "            \"רחל עזריה\": 0.09433962264150944\n" +
            "        },\n" +
            "        \"party_efficiency\": 0.318\n" +
            "    },\n" +
            "    \"מרצ\": {\n" +
            "        \"memeber_efficiency\": {\n" +
            "            \"אילן גילאון\": 0.23076923076923078,\n" +
            "            \"מיכל רוזין\": 0.25443786982248523,\n" +
            "            \"משה (מוסי) רז\": 0.10059171597633136,\n" +
            "            \"עיסאווי פריג`\": 0.22485207100591717,\n" +
            "            \"תמר זנדברג\": 0.1893491124260355\n" +
            "        },\n" +
            "        \"party_efficiency\": 0.338\n" +
            "    },\n" +
            "    \"ש”ס\": {\n" +
            "        \"memeber_efficiency\": {\n" +
            "            \"דוד אזולאי\": 0.1095890410958904,\n" +
            "            \"יואב בן-צור\": 0.1643835616438356,\n" +
            "            \"יעקב מרגי\": 0.1278538812785388,\n" +
            "            \"יצחק וקנין\": 0.1963470319634703,\n" +
            "            \"יצחק כהן\": 0.136986301369863,\n" +
            "            \"מיכאל מלכיאלי\": 0.228310502283105,\n" +
            "            \"משולם נהרי\": 0.0365296803652968\n" +
            "        },\n" +
            "        \"party_efficiency\": 0.31285714285714283\n" +
            "    }\n" +
            "}";

    private float[] yData = { 5, 10, 15, 30, 40 };
    private String[] xData = { "Sony", "Huawei", "LG", "Apple", "Samsung" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        Drawable myBadge = getMyBadge(R.id.mainLayout); TODO from getMyBadge return the resource as drawable
        Drawable myBadge = getResources().getDrawable(R.drawable.rank1);
        toolbar.setLogo(myBadge);
        setSupportActionBar(toolbar);
        mainLayout =  findViewById(R.id.mainLayout);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        getConnection(R.id.mainLayout);
        // TODO remove it
        onConnectionSuccess();
    }

    @Override
    protected void onConnectionSuccess() {
        createEfficiencyChar();
        createMissingChar();
        createProposalsChar();
//        creatingChart();
    }

    private void createEfficiencyChar() {
        JSONObject jsonObj = null;
        try {
           jsonObj = new JSONObject(jsonTest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        createBarChart(R.id.chart1, jsonObj, EFFICIENCY, EFFICIENCY_M, chart1);
    }

    private void createProposalsChar() {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonTest2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        createBarChart(R.id.chart2, jsonObj, PROPOSALS, PROPOSALS_M, chart2);

    }

    private void createMissingChar() {

        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonTest1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        createBarChart(R.id.chart3, jsonObj, MISSING, MISSING_M, chart3);

    }

    private void createBarChart(int char_id, JSONObject parties, String key, String keyMember, Map<String, JSONObject> map) {

        BarChart barChart = (BarChart) findViewById(char_id);

        ArrayList<BarEntry> valueSet = new ArrayList<>();
        ArrayList<String> xAxis = new ArrayList<>();

        try {
            Iterator<?> partyName = parties.keys();
            int counter = 0;
            while( partyName.hasNext() ) {
                final String name = (String)partyName.next();
                // Add party name
                xAxis.add(name);
                // Get party's data
                if ( parties.get(name) instanceof JSONObject ) {
                    JSONObject data = (JSONObject)parties.get(name);
                    float val = Math.round((((Number)data.get(key)).floatValue() * 100 * 100.0) / 100.0);
                    BarEntry entry = new BarEntry(val, counter);
                    // Get value of party
                    valueSet.add(entry);
                    // Go over party members
                    if (data.get(keyMember) instanceof JSONObject ) {
                        JSONObject memData = (JSONObject)data.get(keyMember);
                        final JSONObject dataForShow = new JSONObject();
                        Iterator<?> memName = memData.keys();
                        while(memName.hasNext()) {
                            String nameM = (String)memName.next();
                            String valM = new DecimalFormat("##.##").format(((Number)memData.get(nameM)).floatValue() * 100);
                            dataForShow.put(nameM, valM);
                        }

                        map.put(name,dataForShow);

                        // set a chart value selected listener
                        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

                            @Override
                            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                                // display msg when value selected
                                if (e == null)
                                    return;

                                Toast.makeText(MainActivity.this,
                                        (name + " = " + dataForShow.toString()), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNothingSelected() {

                            }
                        });
                    }
                }
                counter ++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BarDataSet dataSet = new BarDataSet(valueSet, "מפלגות");
        dataSet.setColors(getColors());

        BarData data = new BarData(xAxis, dataSet);
        barChart.setData(data);
        barChart.setDescription("");
        barChart.animateXY(2000, 2000);
        barChart.invalidate();
    }

    protected void creatingChart(List<String> members, List<Float> membersPrec){
        mChart = new PieChart(this);
        // add pie chart to main layout
        mainLayout.addView(mChart, 1000, 500);
        mainLayout.setBackgroundColor(Color.WHITE);

        // configure pie chart
        mChart.setUsePercentValues(true);
        mChart.setDescription(TITLE_PIE);

        // enable hole and configure
        mChart.setDrawHoleEnabled(true);
//        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(10);

        // enable rotation of the chart by touch
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        // set a chart value selected listener
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                // display msg when value selected
                if (e == null)
                    return;

                Toast.makeText(MainActivity.this,
                        xData[e.getXIndex()] + " = " + e.getVal() + "%", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // add data
        addData();

        // customize legends
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }

    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < yData.length; i++)
            yVals1.add(new Entry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        Collections.addAll(xVals, xData);

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "Market Share");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);


        dataSet.setColors(getColors());

        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        // update pie chart
        mChart.invalidate();
    }

    private ArrayList<Integer> getColors() {
        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        return colors;
    }

}
