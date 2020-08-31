(function() { // avoid variables ending up in the global scope

	// page components
	var AlbumsList, ImageList, ImageDetails, NextButton, PreviousButton;
	pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		pageOrchestrator.start(); // initialize the components
		pageOrchestrator.refresh();
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

				row = document.createElement("tr");

				imageCell = document.createElement("td");
				imageTag = document.createElement("img");
				//imageTag.currentSrc = album.iconPath;
				imageTag.setAttribute('src', album.iconPath);
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

					imageList.show(e.target.getAttribute("albumId"));
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
		this.images = null;
		this.currentBlock = 0;
		this.numToShow = 5;

		this.reset = function() {
			this.galleryRow.style.visibility = "hidden";
		}

		this.show = function(albumId) {
			var that = this;

			makeCall("GET", "GetImagesOfAlbum?albumId=" + albumId, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var imagesToShow = JSON.parse(req.responseText);
							if (imagesToShow.length == 0) {
								that.alert.textContent = "No images of this album available!";
								return;
							}
							that.update(imagesToShow); // that visible by closure
						}
					} else {
						that.alert.textContent = message;
					}
				}
			);
		};


		this.update = function(arrayImages) {
			var row, imageThumbnail, titleBody, imageAnchor, imageTag;
			this.galleryBody.innerHTML = ""; // empty the table body
			// build updated list
			var that = this;
			row = document.createElement("tr");

			console.log(i);
			console.log(end);
			if (arrayImages) {
				that.images = arrayImages;
			}
			var i = that.currentBlock * that.numToShow;

			var end = i + that.numToShow;
			if (end > that.images.length) {
				end = that.images.length;
			}


			for (; i < end; i++) {

				var image = that.images[i];
				imageThumbnail = document.createElement("td");
				titleBody = document.createElement("p");
				titleBody.textContent = image.title;
				imageThumbnail.appendChild(titleBody);
				imageAnchor = document.createElement("a");
				imageTag = document.createElement("img");
				imageTag.setAttribute('src', image.filePath);
				imageTag.classList.add("image");
				imageTag.classList.add("img-thumbnail");
				imageAnchor.appendChild(imageTag);
				imageThumbnail.appendChild(imageAnchor);
				row.appendChild(imageThumbnail);

				imageAnchor.setAttribute('imageId', image.id);
				imageAnchor.addEventListener("click", (e) => {
					imageDetails.show(e.currentTarget.getAttribute("imageId"));
				}, false);
				imageAnchor.href = "#";


			}
			that.galleryBody.appendChild(row);

			this.galleryRow.style.visibility = "visible";

		}

		this.next = function() {

			if (((this.currentBlock * this.numToShow) + this.numToShow) < this.images.length) {
				this.currentBlock++;
				this.update(null);

			}
		}

		this.previous = function() {
			if (this.currentBlock > 0) {
				this.currentBlock--;
				this.update(null);

			}
		}

	}

	function NextButton() {
		this.registerEvents = function(orchestrator) {
			document.getElementById("id_nextButton").addEventListener('click', (e) => {

				orchestrator.moveImagesNext();

			});
		}
	}

	function PreviousButton() {
		this.registerEvents = function(orchestrator) {
			document.getElementById("id_previousButton").addEventListener('click', (e) => {
				orchestrator.moveImagesPrevious();

			});

		}
	}

	function ImageDetails(_alert, _imageRow, _imageBody, _descriptionBody, _commentBody) {
		this.alert = _alert;
		this.imageRow = _imageRow;
		this.imageBody = _imageBody;
		this.descriptionBody = _descriptionBody;
		this.commentBody = _commentBody;

		this.reset = function() {


		}

		this.show = function(imageId) {
			var that = this;
			console.log(imageId);
			makeCall("GET", "GetImageAndComments?imageId=" + imageId, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var imageAndComments = JSON.parse(req.responseText);
							console.log(imageAndComments);
							if (imageAndComments.length == 0) {
								that.alert.textContent = "No images of this album available!";
								return;
							}
							that.update(imageAndComments); // that visible by closure
						}
					} else {
						that.alert.textContent = message;
					}
				}
			);
		};


		this.update = function(imageAndComments) {
			var row1, commentRow, imageCell, imageDateCell, imageTag, imageDescription, commentCell1, commentUsername, commentCell2, commentText, commentCell3, commentDate;

			this.imageBody.innerHTML = ""; // empty the table body
			this.commentBody.innerHTML = ""; // empty the table body
			this.descriptionBody.innerHTML = ""; // empty the table body
			// build updated list
			var that = this;
			var image = imageAndComments.image;
			var comments = imageAndComments.comments;


			//image info : title + date
			imageCell = document.createElement("p");
			imageCell = document.createTextNode(image.title);
			that.imageBody.appendChild(imageCell);
			imageDateCell = document.createElement("p");
			imageDateCell = document.createTextNode(image.date);
			that.imageBody.appendChild(imageDateCell);

			//image scr + description
			imageTag = document.createElement("img");
			imageTag.setAttribute('src', image.filePath);
			that.descriptionBody.appendChild(imageTag);
			imageDescription = document.createElement("p");
			imageDescription = document.createTextNode(image.description);
			that.imageBody.appendChild(imageDescription);

			comments.forEach(function(comment) {
				commentRow = document.createElement("div");
				//comments of the image selected
				commentCell1 = document.createElement("div");
				commentUsername = document.createElement("p");
				commentUsername = document.createTextNode(comment.username);
				commentCell1.appendChild(commentUsername);
				commentRow.appendChild(commentCell1);

				commentCell2 = document.createElement("div");
				commentText = document.createElement("p");
				commentText = document.createTextNode(comment.text);
				commentCell2.appendChild(commentText);
				commentRow.appendChild(commentCell2);

				commentCell3 = document.createElement("div");
				commentDate = document.createElement("p");
				commentDate = document.createTextNode(comment.date);
				commentCell3.appendChild(commentDate);
				commentRow.appendChild(commentCell3);

			});
			that.commentBody.appendChild(commentRow);


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
				document.getElementById("id_galleryRow"),
				document.getElementById("id_galleryBody"));

			imageDetails = new ImageDetails(
				alertContainer,
				document.getElementById("id_imageRow"),
				document.getElementById("id_imageBody"),
				document.getElementById("id_descriptionBody"),
				document.getElementById("id_commentBody"));

			nextButton = new NextButton();
			previousButton = new PreviousButton();
			nextButton.registerEvents(this);
			previousButton.registerEvents(this);

			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			})
		};



		this.refresh = function() {
			alertContainer.textContent = "";
			albumsList.reset();
			imageList.reset();
			imageDetails.reset();
			albumsList.show();

		};


		this.moveImagesNext = function() {

			imageList.next();
		};

		this.moveImagesPrevious = function() {

			imageList.previous();
		};
	}
})();
