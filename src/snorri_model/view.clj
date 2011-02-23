(ns snorri-model.view
  "HTML building"
  (:require [appengine-magic.services.user :as user]
            [snorri-model.scrape :as scrape])
  (:use hiccup.core
        hiccup.page-helpers))

(defn index-script []
  (html
     [:script {:type "text/javascript"} "
jQuery.fn.dataTableExt.oSort['numeric-na-asc']  = function(a,b) {
	var x = (a == \"NA\") ? 0 : a;
	var y = (b == \"NA\") ? 0 : b;
	return x - y;
};

jQuery.fn.dataTableExt.oSort['numeric-na-desc'] = function(a,b) {
	var x = (a == \"NA\") ? 0 : a;
	var y = (b == \"NA\") ? 0 : b;
	return y - x;
};

$(document).ready(function(){
	$('#example').dataTable({
  \"aoColumnDefs\": [ { \"sType\": \"numeric-na\", \"aTargets\": [ 1,2,3,4,5,6,7 ] } ],
  \"bInfo\": false,
  \"bJQueryUI\": true,
  \"bPaginate\": false
});
});"]))

(defn symbols-script []
  (html
     [:script {:type "text/javascript"} "
$(document).ready(function(){
	$('#example').dataTable({
  \"bInfo\": false,
  \"bJQueryUI\": true,
  \"bPaginate\": false
});
});"]))

(defn layout
  "Render default page."
  [page-script & content]
  (html
    (doctype :xhtml-strict)
    (xhtml-tag "en"
      [:head
        [:meta {:http-equiv "Content-type"
                :content "text/html; charset=utf-8"}]
        [:title "Snorri-model"]
        [:style {:type "text/css" :title "currentStyle"} "
        @import \"/css/demo_page.css\";
        @import \"/css/demo_table_jui.css\";
        @import \"/css/jquery-ui-1.7.2.custom.css\";"]
        [:script {:type "text/javascript" :src "http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"}]
        [:script {:type "text/javascript" :src "/js/jquery.dataTables.min.js"}]
        page-script]
      [:body#dt_example
        [:div#container
          [:div.full_width.big
            "Snorri-model"]
          [:div#content
            content]]])))

(defn symbol-link
  "External link for a stock symbol."
  [symbol]
  (link-to (scrape/get-scrape-url (h symbol)) (h symbol)))

(defn index
  "Render home page body. Potentially cached."
  [last-date data]
  (html
    [:div.demo_jui
      [:p "Last update: " last-date]
      [:table#example.display {:cellpadding "0" :cellspacing "0" :border "0"}
       [:thead
        [:tr
         [:th "Symbol"] [:th "10yAvgPE"] [:th "1yES"] [:th "5yExpEG"] [:th "Safe5yExpEG"]
         [:th "LAST"] [:th "5yEXP"] [:th "yGAIN"] [:th "ADVISE"]]]
       [:tbody
        (for [{:keys [symbol close avg-pe sum-es eg safe-eg exp gain advise]} data]
               [:tr
                [:td (symbol-link symbol)] [:td avg-pe] [:td sum-es] [:td eg] [:td safe-eg]
                [:td close] [:td exp] [:td gain] [:td advise]])]]]))

(defn symbols
  "Render symbol page body."
  [data]
  (html
    [:div.demo_jui
    [:p
      (link-to (user/logout-url :destination "/") "Logout")]
    [:div#data
     [:form {:method "post" :action "/symbols/"}
      [:fieldset
       [:legend "New Symbol"]
        [:p
         [:input.text {:type "text" :id "symbol" :name "symbol" :maxlength "16"}]]
         [:input {:type "submit" :value "Add"}]]]
     [:br]
     [:table#example.display {:cellpadding "0" :cellspacing "0" :border "0"}
      [:thead
      [:tr
       [:th "Symbol"]
       [:th "Last scrape"]
       [:th "Last result"]
       [:th "# Success"]
       [:th "# Failure"]
       [:th]]]
      [:tbody
      (for [{:keys [symbol last-scrape-date last-scrape-result
                    success-count failure-count]} data]
             [:tr
              [:td (symbol-link symbol)]
              [:td (or last-scrape-date "NEVER")]
              [:td (or last-scrape-result "")]
              [:td (or success-count 0)]
              [:td (or failure-count 0)]
              [:td
               [:form {:method "post" :action (str "/symbols/" (h symbol))}
                 [:input {:type "submit" :value "Delete"}]]]])]]]]))

