module me.lotabout.spi.provider {
  requires me.lotabout.spi.api;

  provides me.lotabout.spi.api.Function with
      me.lotabout.app.provider.FuncMul,
      me.lotabout.app.provider.FuncAdd,
      me.lotabout.app.provider.FuncConcat,
      me.lotabout.app.provider.FuncId;
}
