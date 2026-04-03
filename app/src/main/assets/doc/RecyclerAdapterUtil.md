# RecyclerAdapterUtil - 文档

## 一、概述

- **功能**
  ：Lua层一行代码创建RecyclerView适配器，自动完成数据与视图绑定；支持单类型/多类型布局、数据插入/删除/更新/清空/整批替换，以及条目点击、长按、子控件事件，所有操作同步调用且自带默认动画，无需手动调用notify相关方法。
- **适用场景**：Android开发中各类RecyclerView实现场景，如聊天列表、商品列表、瀑布流、文件管理器、设置页面等。
- **优势**：1. 零样板，Lua表作为数据源，loadlayout语法编写Item布局；2. 零线程，增删改操作全同步，主线程执行安全；3.
  零反射，内部预编译，性能与原生写法一致。

## 二、启用方式

在项目`settings.json`文件中添加工具类到全局工具列表：

```json
{
  "application": {
    "label": "My App",
    "debugmode": true
  },
  "global_utils": [
    "RecyclerAdapterUtil"
  ]
}
```

## 三、核心函数

### 1. createAdapter（创建单类型适配器）

- **Lua签名**：`createAdapter(data, listItem, method)`
- **功能**：快速创建单布局类型的RecyclerView适配器，完成数据与视图绑定
- **参数**：
    - `data`(table)：数据源（Lua表，必填）
    - `listItem`(table/string)：Item布局（inline布局表或布局文件路径，必填）
    - `method`(table)：回调方法表，核心含onBindViewHolder（必填）
- **返回值**：(userdata) 适配器对象，可直接绑定到RecyclerView

### 2. createMultiTypeAdapter（创建多类型适配器）

- **Lua签名**：`createMultiTypeAdapter(data, typeMap, method)`
- **功能**：创建支持多布局类型的RecyclerView适配器，适配不同样式条目场景
- **参数**：
    - `data`(table)：数据源（Lua表，必填）
    - `typeMap`(table)：类型与布局映射表（键为类型标识，值为布局路径/表，必填）
    - `method`(table)：回调方法表，含getItemType、onBindViewHolder（必填）
- **返回值**：(userdata) 适配器对象

### 3. insertItem（插入单条数据）

- **Lua签名**：`insertItem(adapter, pos, item)`
- **功能**：在指定位置插入单条数据，自带默认动画，无需手动刷新
- **参数**：
    - `adapter`(userdata)：目标适配器对象（必填）
    - `pos`(number)：插入位置索引（必填）
    - `item`(table)：待插入数据条目（必填）
- **返回值**：无

### 4. removeItem（删除单条数据）

- **Lua签名**：`removeItem(adapter, pos)`
- **功能**：删除指定位置的数据条目，自带默认动画
- **参数**：
    - `adapter`(userdata)：目标适配器对象（必填）
    - `pos`(number)：待删除数据位置索引（必填）
- **返回值**：无

### 5. updateItem（更新单条数据）

- **Lua签名**：`updateItem(adapter, pos, item)`
- **功能**：替换指定位置的数据条目，自动刷新视图
- **参数**：
    - `adapter`(userdata)：目标适配器对象（必填）
    - `pos`(number)：待更新数据位置索引（必填）
    - `item`(table)：新数据条目（必填）
- **返回值**：无

### 6. addItem（尾部追加数据）

- **Lua签名**：`addItem(adapter, item)`
- **功能**：在数据源尾部追加单条数据，自带默认动画
- **参数**：
    - `adapter`(userdata)：目标适配器对象（必填）
    - `item`(table)：待追加数据条目（必填）
- **返回值**：无

### 7. clearData（清空数据源）

- **Lua签名**：`clearData(adapter)`
- **功能**：清空适配器所有数据源，视图同步刷新
- **参数**：`adapter`(userdata)：目标适配器对象（必填）
- **返回值**：无

### 8. updateAdapterData（整批替换数据）

- **Lua签名**：`updateAdapterData(adapter, newData)`
- **功能**：用新数据源整批替换原有数据，一次性刷新视图
- **参数**：
    - `adapter`(userdata)：目标适配器对象（必填）
    - `newData`(table)：全新数据源（必填）
- **返回值**：无

### 9. getItem（读取单条数据）

- **Lua签名**：`getItem(adapter, pos)`
- **功能**：获取适配器指定位置的数据条目
- **参数**：
    - `adapter`(userdata)：目标适配器对象（必填）
    - `pos`(number)：数据位置索引（必填）
- **返回值**：(table) 指定位置的数据条目

### 10. getAdapterData（读取完整数据源）

- **Lua签名**：`getAdapterData(adapter)`
- **功能**：获取适配器数据源的完整副本
- **参数**：`adapter`(userdata)：目标适配器对象（必填）
- **返回值**：(table) 数据源完整副本

### 11. findItemPosition（条件查找数据位置）

- **Lua签名**：`findItemPosition(adapter, predicate)`
- **功能**：根据自定义条件查找数据条目对应的位置索引
- **参数**：
    - `adapter`(userdata)：目标适配器对象（必填）
    - `predicate`(function)：查找条件函数（返回true时匹配成功，必填）
- **返回值**：(number) 匹配条目位置索引，无匹配返回nil

## 四、单类型快速上手

30行精简代码即可实现基础列表功能，运行后可直接操作数据刷新视图：

```lua
-- 1. 准备数据源（普通Lua表）
local data = {
  {title = "Apple",  content = "Sweet"},
  {title = "Banana", content = "Soft"},
}

-- 2. 定义Item布局（inline写法，也可引用独立布局文件）
local itemView = {
  LinearLayout,
  orientation = "vertical",
  padding = "16dp",
  {
    TextView, id = "tv_title", textSize = "18sp",
  },
  {
    TextView, id = "tv_content", textSize = "14sp",
  },
}

-- 3. 创建单类型适配器，绑定数据与视图
local adapter = createAdapter(
  data,
  itemView,
  {
    onBindViewHolder = function(holder, pos, views, item)
      views.tv_title.text   = item.title
      views.tv_content.text = item.content
    end
  }
)

-- 4. 绑定适配器到RecyclerView并设置布局管理器
recyclerView.setAdapter(adapter)
recyclerView.setLayoutManager(LinearLayoutManager(activity))
```

运行效果：列表立即展示2条数据，后续调用addItem、insertItem等方法，均会自带默认动画同步刷新视图。

## 五、多类型适配（聊天场景示例）

适配不同样式条目场景，以聊天列表为例，支持发送消息、接收消息、图片消息三种类型：

```lua
-- 1. 定义类型与布局的映射表（类型标识对应布局路径）
local typeMap = {
  [0] = "item_send.lua",   -- 自己发送的文字消息布局
  [1] = "item_recv.lua",   -- 接收的文字消息布局
  [2] = "item_pic.lua",    -- 图片消息布局
}

-- 2. 准备多类型数据源，每条数据带type字段标识类型
local chatData = {
  {type = 0, text = "Hello"},
  {type = 1, text = "Hi"},
  {type = 2, img = "http://a.com/a.jpg"},
}

-- 3. 创建多类型适配器
local adapter = createMultiTypeAdapter(
  chatData,
  typeMap,
  {
    -- 定义条目类型判断逻辑，返回数据对应的类型标识
    getItemType = function(pos, item) return item.type end,
    -- 视图绑定逻辑，适配不同类型条目
    onBindViewHolder = function(holder, pos, views, item)
      if item.text then
        views.tv_text.text = item.text
      else
        loadImage(views.iv_img, item.img) -- 复用图片加载工具类
      end
    end
  }
)

-- 4. 绑定到RecyclerView
recyclerView.setAdapter(adapter)
recyclerView.setLayoutManager(LinearLayoutManager(activity))
```

## 六、事件绑定（点击/长按/子控件事件）

在onBindViewHolder回调中直接绑定各类事件，支持条目整体与子控件事件：

```lua
onBindViewHolder = function(holder, pos, views, item)
  -- 1. 条目整体点击事件
  holder.itemView.onClick = function()
    toast("点击条目，位置：" .. pos)
  end

  -- 2. 条目长按事件
  holder.itemView.onLongClick = function()
    adapter.removeItem(pos) -- 长按删除当前条目
    return true
  end

  -- 3. 子控件事件（示例：删除按钮）
  views.btn_delete.onClick = function()
    adapter.removeItem(pos) -- 点击子控件删除条目
  end
end
```

> 注意：`holder` 是Java层ViewHolder对象，`views` 是Lua层控件表，控件已按布局id自动注入，可直接调用。

## 七、动态增删改实战示例

实现添加、批量更新、清空三个常用功能，可直接复用：

```lua
-- 1. 顶部插入新条目
btnAdd.onClick = function()
  local newItem = {title = "New Item", content = os.date()}
  adapter.insertItem(0, newItem)   -- 插入到列表顶部
  recyclerView.smoothScrollToPosition(0) -- 滚动到顶部显示新条目
end

-- 2. 批量更新所有条目内容
btnUpdate.onClick = function()
  local allData = adapter.getAdapterData() -- 获取数据源副本
  for i, item in ipairs(allData) do
    item.title = "Updated " .. i -- 批量修改数据
  end
  adapter.updateAdapterData(allData) -- 整批替换刷新
end

-- 3. 清空列表所有数据
btnClear.onClick = function()
  adapter.clearData() -- 清空数据源，视图同步刷新
end
```

## 八、布局写法小贴士

1. Item布局根节点宽高无需手动写死，最终由RecyclerView的LayoutManager决定适配规则；
2. 布局中需设置背景、圆角、阴影时，直接通过`background`属性引用drawable资源即可；
3. 多类型布局场景下，不同样式Item的控件id可以重复，Lua层会按当前布局实际控件注入views，不影响使用。

## 九、最佳实践

1. 图片加载优化：Item内图片加载直接使用`loadImage(views.iv_img, url)`，底层Glide会自动完成图片回收，避免内存泄漏；
2. 大数据适配：面对大量数据时采用分页加载策略，每次调用addItem追加50条以内数据，避免一次性调用updateAdapterData替换超大数据源，提升流畅度；
3. 内存回收：在Activity的`onDestroy`生命周期方法中，调用`recyclerView.setAdapter(nil)`
   ，解除适配器与RecyclerView绑定，帮助系统及时GC，避免内存泄漏。

## 十、Q & A

- 问题：修改数据源后视图未刷新
  原因/解决：是否直接修改了原始Lua数据源表？需使用工具类提供的insertItem、removeItem、updateItem等标准接口，接口会自动触发视图刷新，直接改表不会同步到视图；
- 问题：多类型列表出现条目样式错位
  原因/解决：getItemType回调返回的类型标识，必须在typeMap中预先定义对应布局，未定义的类型会导致样式匹配失败；
- 问题：Item内图片加载出现闪烁
  原因/解决：给loadImage方法添加签名或指定磁盘缓存策略为`"RESOURCE"`，提升图片加载复用率，解决闪烁问题。