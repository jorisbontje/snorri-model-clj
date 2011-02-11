(ns snorri-model.test.scrape
  (:use [snorri-model.scrape] :reload)
  (:use [clojure.test]
        [clojure.contrib.string :only [substring?]]))

(deftest should-give-valid-scrape-url
  (is (substring? "BBBY" (get-scrape-url "BBBY"))))

(def html-close
  "<tr><td>Previous Close</td><td style=\"text-align:right\">164.09</td></tr>")

(deftest should-extract-close
  (is (= 164.09 (extract-close html-close))))

(deftest should-return-nil-extract-close
  (is (= nil (extract-close "xyzzy"))))

(deftest should-filter-span
  (is (= "112.80" (filter-span "112.80")))
  (is (= "-185.80" (filter-span "<span style=\"color:red\">-185.80</span>"))))

(def html-avg10yPE
  "<table><thead><tr><th>&nbsp;</th><th style=\"text-align:right\">Avg P/E</th><th style=\"text-align:right\">Price/ Sales</th><th style=\"text-align:right\">Price/ Book</th><th style=\"text-align:right\">Net Profit Margin (%)</th></tr></thead><tbody><tr><td>09/10</td><td style=\"text-align:right\">15.10</td><td style=\"text-align:right\">4.14</td><td style=\"text-align:right\">5.60</td><td style=\"text-align:right\">21.5</td></tr><tr><td>09/09</td><td style=\"text-align:right\">13.30</td><td style=\"text-align:right\">3.86</td><td style=\"text-align:right\">5.19</td><td style=\"text-align:right\">19.2</td></tr><tr><td>09/08</td><td style=\"text-align:right\">24.00</td><td style=\"text-align:right\">3.09</td><td style=\"text-align:right\">5.11</td><td style=\"text-align:right\">16.3</td></tr><tr><td>09/07</td><td style=\"text-align:right\">26.50</td><td style=\"text-align:right\">5.55</td><td style=\"text-align:right\">9.21</td><td style=\"text-align:right\">14.2</td></tr><tr><td>09/06</td><td style=\"text-align:right\">29.20</td><td style=\"text-align:right\">3.50</td><td style=\"text-align:right\">6.59</td><td style=\"text-align:right\">10.3</td></tr><tr><td>09/05</td><td style=\"text-align:right\">24.10</td><td style=\"text-align:right\">3.27</td><td style=\"text-align:right\">5.98</td><td style=\"text-align:right\">9.5</td></tr><tr><td>09/04</td><td style=\"text-align:right\">38.80</td><td style=\"text-align:right\">1.74</td><td style=\"text-align:right\">2.88</td><td style=\"text-align:right\">3.2</td></tr><tr><td>09/03</td><td style=\"text-align:right\">89.80</td><td style=\"text-align:right\">1.21</td><td style=\"text-align:right\">1.80</td><td style=\"text-align:right\">1.1</td></tr><tr><td>09/02</td><td style=\"text-align:right\">112.80</td><td style=\"text-align:right\">0.93</td><td style=\"text-align:right\">1.29</td><td style=\"text-align:right\">1.1</td></tr><tr><td>09/01</td><td style=\"text-align:right\"><span style=\"color:red\">-185.80</span></td><td style=\"text-align:right\">NA</td><td style=\"text-align:right\">1.39</td><td style=\"text-align:right\"><span style=\"color:red\">-0.7</span></td></tr></tbody></table>")

(def html-avg10yPE-with-NA
  "<table><thead><tr><th>&nbsp;</th><th style=\"text-align:right\">Avg P/E</th><th style=\"text-align:right\">Price/ Sales</th><th style=\"text-align:right\">Price/ Book</th><th style=\"text-align:right\">Net Profit Margin (%)</th></tr></thead><tbody><tr><td>06/10</td><td style=\"text-align:right\">NA</td><td style=\"text-align:right\">NA</td><td style=\"text-align:right\">NA</td><td style=\"text-align:right\"><span style=\"color:red\">-20.8</span></td></tr><tr><td>06/09</td><td style=\"text-align:right\"><span style=\"color:red\">-10.80</span></td><td style=\"text-align:right\">0.35</td><td style=\"text-align:right\">1.55</td><td style=\"text-align:right\"><span style=\"color:red\">-12.0</span></td></tr><tr><td>12/08</td><td style=\"text-align:right\"><span style=\"color:red\">-14.90</span></td><td style=\"text-align:right\">NA</td><td style=\"text-align:right\">1.79</td><td style=\"text-align:right\">NA</td></tr><tr><td>12/07</td><td style=\"text-align:right\"><span style=\"color:red\">-16.70</span></td><td style=\"text-align:right\">NA</td><td style=\"text-align:right\">3.46</td><td style=\"text-align:right\">NA</td></tr><tr><td>12/06</td><td style=\"text-align:right\">4.30</td><td style=\"text-align:right\">0.22</td><td style=\"text-align:right\">1.08</td><td style=\"text-align:right\">8.0</td></tr><tr><td>12/05</td><td style=\"text-align:right\">23.80</td><td style=\"text-align:right\">0.50</td><td style=\"text-align:right\">2.71</td><td style=\"text-align:right\">2.5</td></tr><tr><td>12/04</td><td style=\"text-align:right\">124.00</td><td style=\"text-align:right\">1.31</td><td style=\"text-align:right\">7.37</td><td style=\"text-align:right\">1.1</td></tr><tr><td>12/03</td><td style=\"text-align:right\">18.30</td><td style=\"text-align:right\">1.51</td><td style=\"text-align:right\">10.22</td><td style=\"text-align:right\">6.7</td></tr><tr><td>12/02</td><td style=\"text-align:right\"><span style=\"color:red\">-35.20</span></td><td style=\"text-align:right\">1.65</td><td style=\"text-align:right\">20.00</td><td style=\"text-align:right\"><span style=\"color:red\">-3.6</span></td></tr><tr><td>12/01</td><td style=\"text-align:right\"><span style=\"color:red\">-13.20</span></td><td style=\"text-align:right\">1.63</td><td style=\"text-align:right\">14.48</td><td style=\"text-align:right\"><span style=\"color:red\">-13.9</span></td></tr></tbody></table>")

(deftest should-extract-avg10yPE
  (is (= [15.1 13.3 24.0 26.5 29.2 24.1 38.8 89.8 112.8 -185.8] (extract-avg10yPE html-avg10yPE))))

(deftest should-extract-avg10yPE-with-NA
  (is (= [nil -10.8 -14.9 -16.7 4.3 23.8 124.0 18.3 -35.2 -13.2] (extract-avg10yPE html-avg10yPE-with-NA))))

(def html-1yES
  "<tr><td>Actual</td><td style=\"text-align:right\">3.67</td><td style=\"text-align:right\">3.33</td><td style=\"text-align:right\">3.51</td><td style=\"text-align:right\">4.64</td><td style=\"text-align:right\">6.43</td></tr>")

(def html-1yES-with-negatives
  "<tr><td>Actual</td><td style=\"text-align:right\"><span style=\"color:red\">-0.02</span></td><td style=\"text-align:right\"><span style=\"color:red\">-0.02</span></td><td style=\"text-align:right\"><span style=\"color:red\">-0.03</span></td><td style=\"text-align:right\">0.10</td><td style=\"text-align:right\">0.25</td></tr>")

(deftest should-extract-1yES
  (is (= [3.33 3.51 4.64 6.43] (extract-1yES html-1yES))))

(deftest should-extract-1yES-with-negatives
  (is (= [-0.02 -0.03 0.1 0.25] (extract-1yES html-1yES-with-negatives))))

(def html-5yEG
  "<tr><td>Company</td><td style=\"text-align:right\"><span style=\"color:red\">-22.90%</span></td><td style=\"text-align:right\"><span style=\"color:green\">+117.30%</span></td><td style=\"text-align:right\"><span style=\"color:green\">+21.20%</span></td><td style=\"text-align:right\"><span style=\"color:green\">+15.00%</span></td><td style=\"text-align:right\">15.90</td></tr>")

(deftest should-extract-5yEG
  (is (= 15.00 (extract-5yEG html-5yEG))))
