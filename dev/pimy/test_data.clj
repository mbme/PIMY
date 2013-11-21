(ns pimy.test-data
  (:require [clojure.string :as string]
            [pimy.storage :as storage]
            [clojure.tools.logging :as log]))

(def source
  "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque et ipsum at elit euismod pharetra quis vel tortor. Nullam ultricies lectus et auctor pulvinar. Nullam tristique ligula nibh. Nam feugiat ante quis dolor consectetur porta. Donec fermentum hendrerit sem a tristique. Sed ante lectus, porta quis justo at, elementum bibendum mauris. Fusce hendrerit venenatis tortor, sed varius dui vulputate in. Nullam mattis fermentum eros, non tempor erat vestibulum non. Donec congue et nunc vitae tristique. Integer ut elementum neque. Nulla commodo molestie tortor, id condimentum turpis malesuada quis. Aenean a augue hendrerit, accumsan nisi sit amet, iaculis eros.

Sed non lectus nec justo ullamcorper tempor. Sed justo turpis, posuere in consectetur ut, dapibus nec urna. Pellentesque sodales metus et tincidunt fermentum. Integer sed turpis sit amet justo lobortis semper vitae at nulla. Duis sit amet auctor purus. Cras venenatis, magna eget faucibus pellentesque, turpis lorem aliquet nisi, eu pellentesque est tortor ut nibh. Fusce a rutrum elit. Ut ut metus vel dolor elementum mollis nec quis est. Interdum et malesuada fames ac ante ipsum primis in faucibus. Cras eget facilisis urna. Duis varius ligula ipsum, eget fringilla urna vulputate sit amet. Vivamus tempor enim vitae nunc posuere suscipit. Curabitur in elementum orci, nec condimentum justo. Duis vel leo quis arcu fermentum auctor.

Існує багато варіацій уривків з Lorem Ipsum, але більшість з них зазнала певних змін на кшталт жартівливих вставок або змішування слів, які навіть не виглядають правдоподібно. Якщо ви збираєтесь використовувати Lorem Ipsum, ви маєте упевнитись в тому, що всередині тексту не приховано нічого, що могло б викликати у читача конфуз. Більшість відомих генераторів Lorem Ipsum в Мережі генерують текст шляхом повторення наперед заданих послідовностей Lorem Ipsum. Принципова відмінність цього генератора робить його першим справжнім генератором Lorem Ipsum. Він використовує словник з більш як 200 слів латини та цілий набір моделей речень - це дозволяє генерувати Lorem Ipsum, який виглядає осмислено. Таким чином, згенерований Lorem Ipsum не міститиме повторів, жартів, нехарактерних для латини слів.

Vestibulum sed eleifend enim. Sed commodo sem odio, eu vehicula metus aliquet vel. Phasellus vehicula fringilla erat, sed ultrices enim interdum at. Nullam eu magna in nunc lobortis scelerisque. Nunc euismod feugiat nibh ut luctus. Sed eu dui quis quam condimentum mattis a ut risus. Quisque tempor lectus ac massa consequat sodales. Maecenas fermentum, turpis vel iaculis feugiat, arcu urna aliquet quam, ut vehicula nibh lorem eget purus. Donec enim dui, semper ut dapibus sed, cursus eget diam. Vivamus iaculis mattis tellus a commodo. Morbi id diam eget ligula auctor rutrum et eget lorem. Cras dapibus, diam ut varius luctus, augue odio rutrum sem, sed aliquam est purus vitae tellus.")

(def words
  (->
    source
    (string/lower-case)
    (string/replace #"(\n|\.|\,)" " ")
    (string/split #"\s+")
    (distinct)
    ))

(def paragraphs
  (string/split source #"\n+"))

(defn- rand-in-range
  [start end]
  (->
    (rand)
    (* (- end start))
    (+ start)
    (Math/round)
    ))

(defn- gen-list [start end items]
  (take (rand-in-range start end)
    (repeatedly #(rand-nth items))
    ))

(defn- gen-title []
  (string/capitalize (string/join " " (gen-list 2 10 words))))

(defn- gen-tags []
  (vec (gen-list 1 4 words)))

(defn- gen-text []
  (string/join "\n\n" (gen-list 1 8 paragraphs)))

(defn gen-record []
  {:title (gen-title)
   :text (gen-text)
   :tags (gen-tags)
   })

; generate data
(def records-count 100)
(dotimes [_ records-count] (storage/create-record (gen-record)))

(log/info "Generated" records-count "test records")
