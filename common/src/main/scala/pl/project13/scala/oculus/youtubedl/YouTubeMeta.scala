package pl.project13.scala.oculus.youtubedl

import org.json4s.DefaultFormats

/**
{
  "upload_date" : "20101223",
  "playlist" : null,
  "description" : "Player : DOLCE",
  "format" : "18 - 360x640",
  "url" : "http://r1---sn-2a0noxu-ajtl.c.youtube.com/videoplayback?ipbits=8&ratebypass=yes&expire=1382843106&fexp=935500%2C937600%2C902562%2C916625%2C924616%2C924610%2C907231&upn=Cn3aviLv2r4&source=youtube&itag=18&key=yt5&sparams=cp%2Cid%2Cip%2Cipbits%2Citag%2Cratebypass%2Csource%2Cupn%2Cexpire&ms=au&ip=87.114.39.10&mt=1382819559&mv=m&sver=3&id=06376f432f056399&cp=U0hXR1hPUV9FUENON19QSVVCOks3Z3lnX0tUa2N1&signature=ADCD420A8283522A139961004F8C8ACB603B726B.947804D3BE43B604227B1B9AFBFF63E8725A17F7",
  "title" : "beatmania IIDX 18 Resort Anthem - 冥(A)",
  "id" : "BjdvQy8FY5k",
  "thumbnail" : "https://i1.ytimg.com/vi/BjdvQy8FY5k/hqdefault.jpg",
  "ext" : "mp4",
  "stitle" : "beatmania IIDX 18 Resort Anthem - 冥(A)",
  "extractor" : "youtube",
  "annotations" : null,
  "uploader" : "hd6890",
  "duration" : "159",
  "fulltitle" : "beatmania IIDX 18 Resort Anthem - 冥(A)",
  "player_url" : null,
  "age_limit" : 0,
  "uploader_id" : "hd6890",
  "subtitles" : null,
  "playlist_index" : null
}
 */
case class YouTubeMeta(
  upload_date: String,
  description: String,
  format: String,
  url: String,
  title: String,
  id: String,
  thumbnail: String,
  ext: String,
  stitle: String,
  extractor: String,
  annotations: String,
  uploader: String,
  duration: String,
  fulltitle: String,
  player_url: String,
  age_limit: String,
  uploader_id: String
)

object YouTubeMeta {
  implicit val formats = DefaultFormats

  def from(s: String): YouTubeMeta = {
    import org.json4s._
    import org.json4s.jackson.JsonMethods._


    parse(s).extract[YouTubeMeta]
  }
}