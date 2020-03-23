package main

import (
	"log"
	"net/http"
	"os"
	"time"

	"github.com/gorilla/mux"
	"github.com/gorilla/websocket"
)

func now(w http.ResponseWriter, r *http.Request) {
	log.Println("requesting for websocket connection.")
	up := websocket.Upgrader{}
	conn, err := up.Upgrade(w, r, nil)
	if err != nil {
		log.Print(err)
		log.Println("Failed to upgrade to websocket")
		return
	}
	go func() {
		defer conn.Close()
		ticker := time.NewTicker(1 * time.Second)
		for {
			select {
			case <-ticker.C:
				err := conn.WriteMessage(websocket.TextMessage, []byte(time.Now().String()))
				if err != nil {
					log.Print(err)
					break
				}
			}
		}
	}()
}

func main() {
	wd, err := os.Getwd()
	if err != nil {
		log.Fatal(err)
	}

	route := mux.NewRouter()
	route.HandleFunc("/live", now)
	route.PathPrefix("/").Handler(http.FileServer(http.Dir(wd + "/static/")))
	server := http.Server{
		Addr:    ":8080",
		Handler: route,
	}
	log.Fatal(server.ListenAndServe())
}
