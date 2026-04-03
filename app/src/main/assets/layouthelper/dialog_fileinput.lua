local bindClass = luajava.bindClass
local LinearLayoutCompat = bindClass "androidx.appcompat.widget.LinearLayoutCompat"
local EditText = bindClass "android.widget.EditText"

return {
  LinearLayoutCompat,
  layout_width = -1,
  layout_height = -1,
  {
    EditText,
    layout_width = -1,
    singleLine = true,
    hint = "请输入内容",
    layout_margin="26dp",
    layout_marginBottom="8dp",
    id = "content",
  },
}