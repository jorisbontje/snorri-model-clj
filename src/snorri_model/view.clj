(ns snorri-model.view
  "HTML building"
  (:require [appengine-magic.services.user :as user]
            [snorri-model.scrape :as scrape])
  (:use hiccup.core
        hiccup.page-helpers))

(defn layout
  "Render default page."
  [& content]
  (html
    (doctype :xhtml-strict)
    (xhtml-tag "en"
      [:head
        [:meta {:http-equiv "Content-type"
                :content "text/html; charset=utf-8"}]
        [:title "Snorri-model"]]
      [:body
        [:div.container
          [:div#header
            [:div#title
              [:h1 "Snorri-model"]]
          [:div#content
            content]]]])))

(defn symbol-link
  "External link for a stock symbol."
  [symbol]
  (link-to (scrape/get-scrape-url (h symbol)) (h symbol)))

(defn index
  "Render home page body."
  [last-date data]
  (html
    [:div#data
     [:p "Last update: " last-date]
     [:table {:border 1}
      [:tr
       [:th "Symbol"] [:th "10yAvgPE"] [:th "1yES"] [:th "5yExpEG"] [:th "Safe5yExpEG"]
       [:th "LAST"] [:th "5yEXP"] [:th "yGAIN"] [:th "ADVISE"]]
      (for [{:keys [symbol close avg-pe sum-es eg safe-eg exp gain advise]} data]
             [:tr
              [:td (symbol-link symbol)] [:td avg-pe] [:td sum-es] [:td eg] [:td safe-eg]
              [:td close] [:td exp] [:td gain] [:td advise]])]]))

(defn symbols
  "Render symbol page body."
  [data]
  (html
    [:div#admin
      (link-to (user/logout-url :destination "/") "Logout")]
    [:div#data
     [:form {:method "post" :action "/symbols/"}
      [:fieldset
       [:legend "New Symbol"]
        [:p
         [:input.text {:type "text" :id "symbol" :name "symbol" :maxlength "16"}]]
         [:input {:type "submit" :value "Add"}]]]
     [:table
      [:tr
       [:th "Symbol"]
       [:th "Last scrape"]
       [:th "Last result"]
       [:th "# Success"]
       [:th "# Failure"]]
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
                 [:input {:type "submit" :value "Delete"}]]]])]]))

