(ns snorri-model.view
  (:require [appengine-magic.services.user :as user]
            [snorri-model.scrape :as scrape])
  (:use hiccup.core
        hiccup.page-helpers))

(defn layout [& content]
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
            (if (user/user-logged-in?)
              (link-to (user/logout-url) "Logout")
              (link-to (user/login-url) "Login"))]
          [:div#content
            content]]])))

(defn symbol-link [symbol]
  (link-to (scrape/get-scrape-url (h symbol)) (h symbol)))

(defn index [data]
  (html
    [:div#data
     [:table {:border 1}
      [:tr
       [:th "Symbol"] [:th "10yAvgPE"] [:th "1yES"] [:th "EG"] [:th "Safe5yExpEG"]
       [:th "LAST"] [:th "5yEXP"] [:th "yGAIN"] [:th "ADVISE"]]
      (for [{:keys [symbol close pe es eg safe-eg exp gain advise]} data]
             [:tr
              [:td (symbol-link symbol)] [:td pe] [:td es] [:td eg] [:td safe-eg]
              [:td close] [:td exp] [:td gain] [:td advise]])]]))

(defn symbols [data]
  (html
    [:div#data
     [:table
      [:tr
       [:th "Symbol"]]
      (for [{:keys [symbol]} data]
             [:tr
              [:td (symbol-link symbol)]
              [:td
               [:form {:method "post" :action (str "/symbols/" (h symbol))}
                 [:input {:type "submit" :value "Delete"}]]]])]
     [:form {:method "post" :action "/symbols/"}
      [:fieldset
       [:legend "New Symbol"]
        [:p
         [:input.text {:type "text" :id "symbol" :name "symbol" :maxlength "16"}]]
         [:input {:type "submit" :value "Add"}]]]]))

