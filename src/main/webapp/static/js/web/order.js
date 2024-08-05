function Order(goodsCode, skuId, skuSpecPropId, skuSpecPropValueId, specPropValue, quantity, color, size) {
    this.goodsCode = goodsCode;
    this.quantity = quantity;
    this.color = color;
    this.size = size;
    this.skuId = skuId;
    this.skuSpecPropId = skuSpecPropId;
    this.specPropValue = specPropValue;
    this.skuSpecPropValueId = skuSpecPropValueId;
};

