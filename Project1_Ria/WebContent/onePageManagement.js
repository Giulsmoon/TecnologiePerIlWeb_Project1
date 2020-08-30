(function() { // avoid variables ending up in the global scope

	// page components
	var AlbumsList, ImageList,
		pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();
		} // display initial content
	}, false);


	// Constructors of view components



	function AlbumsList(_alert, _albumsRow, _albumsBody) {
		this.alert = _alert;
		this.albumsRow = _albumsRow;
		this.albumsBody = _albumsBody;

		this.reset = function() {
			this.albumsRow.style.visibility = "hidden";
		}

		this.show = function(next) {
			var that = this;
			makeCall("GET", "GetAlbumList", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var albumsToShow = JSON.parse(req.responseText);
							console.log(req.responseText);
							console.log(albumsToShow);
							if (albumsToShow.length == 0) {
								that.alert.textContent = "No albums available!";
								return;
							}
							that.update(albumsToShow); // that visible by closure
							if (next) next(); // show the default element of the list if present
						}
					} else {
						that.alert.textContent = message;
					}
				}
			);
		};


		this.update = function(arrayAlbums) {
			var elem, i, row, imageCell, imageTag, titleCell, linkTitle, dateCell, dateBody, titleAnchor;
			this.albumsBody.innerHTML = ""; // empty the table body
			// build updated list
			var that = this;
			arrayAlbums.forEach(function(album) {
				console.log(album); 
				row = document.createElement("tr");
				
				imageCell = document.createElement("td");
				imageTag = document.createElement("img");
				//imageTag.currentSrc = album.iconPath;
				imageTag.setAttribute( 'src', album.iconPath);
				imageTag.classList.add("img-thumbnail");
				imageCell.appendChild(imageTag);
				row.appendChild(imageCell);
				
				titleCell = document.createElement("td");
				titleAnchor = document.createElement("a");
				linkTitle = document.createTextNode(album.title);
				titleAnchor.appendChild(linkTitle);
				titleCell.appendChild(titleAnchor);
				row.appendChild(titleCell);

				titleAnchor.setAttribute('albumId', album.id);
				titleAnchor.addEventListener("click", (e) => {

					ImageList.show(e.target.getAttribute("albumId"));
				}, false);
				titleAnchor.href = "#";
				
				dateCell = document.createElement("td");
				dateBody = document.createElement("p");
				dateBody = document.createTextNode(album.date);
				dateCell.appendChild(dateBody);
				row.appendChild(dateCell);
				
				that.albumsBody.appendChild(row);

			});
			this.albumsRow.style.visibility = "visible";

		}



	}


	function ImageList(_alert, _galleryRow, _galleryBody) {
		this.alert = _alert;
		this.galleryRow = _galleryRow;
		this.galleryBody = _galleryBody;

		this.reset = function() {
			this.galleryRow.style.visibility = "hidden";
		}

		this.show = function(next) {
			var that = this;
			makeCall("GET", "GetImagesOfAlbum", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var imagesToShow = JSON.parse(req.responseText);
							if (imagesToShow.length == 0) {
								that.alert.textContent = "No images of this album available!";
								return;
							}
							that.update(imagesToShow, 0, 5); // that visible by closure
							if (next) next(); // show the default element of the list if present
						}
					} else {
						that.alert.textContent = message;
					}
				}
			);
		};


		this.update = function(arrayImages, currentBlock, numToShow) {
			var row, imageThumbnail, titleBody, imageAnchor, imageTag;
			this.albumsBody.innerHTML = ""; // empty the table body
			// build updated list
			var that = this;
			row = document.createElement("tr");
			var i = currentBlock *numToShow;
			var end = i + numToShow;

			for (; i < end; i++) {

				var image = arrayImages[i];
				imageThumbnail = document.createElement("td");
				titleBody = document.createElement("p");
				titleBody.textContent = image.title;
				imageAnchor = document.createElement("a");
				imageTag = document.createElement("img");
				imageTag.currentSrc = image.filePath;
				imageTag.classList.add("image");
				imageTag.classList.add("img-thumbnail");
				imageAnchor.appendChild(imageTag);

				imageAnchor.setAttribute('imageId', image.id);
				imageAnchor.addEventListener("click", (e) => {

					imageDetails.show(e.target.getAttribute("imageId"));
				}, false);
				imageAnchor.href = "#";
				row.appendChild(imageThumbnail);

				
			}
			that.galleryBody.appendChild(row);

			this.galleryRow.style.visibility = "visible";

		}



	}


	function NextButton() {
		this.registerEvents = function(orchestrator) {
			nextForm: document.getElementById("id_nextForm"),
			this.nextForm.querySelector("input[type='button'].next").addEventListener('click', (e) => {
				var form = e.target.closest("form");

			});

		}
	}
	function PreviousButton() {
		this.registerEvents = function(orchestrator) {
			previousForm: document.getElementById("id_previousForm"),
			this.previousForm.querySelector("input[type='button'].previous").addEventListener('click', (e) => {
				var form = e.target.closest("form");

			});

		}
	}



	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");
		this.start = function() {
			

			albumsList = new AlbumsList(
				alertContainer,
				document.getElementById("id_albumsRow"),
				document.getElementById("id_albumsBody"));

			imageList = new ImageList(
				alertContainer,
				document.getElementById("id_imageRow"),
				document.getElementById("id_imageBody"));

			

			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			})
		};


		this.refresh = function(currentAlbum) {
			alertContainer.textContent = "";
			albumsList.reset();
			albumsList.show();
			
			//imageList.show(function() {
				//imageList.autoclick(currentAlbum);
			//}); // closure preserves visibility of this
			
		};
	}
})();
