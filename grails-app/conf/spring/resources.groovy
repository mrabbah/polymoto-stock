
	
// Place your Spring DSL code here

beans = { 
    
    mainWindow(com.choranet.commun.MainWindow) { bean ->
        bean.scope = "prototype"
        bean.autowire = "byName"
    }
    
    entrepotWindow(com.choranet.gesticom.EntrepotWindow) { bean ->
        bean.scope = "prototype"
        bean.autowire = "byName"
    }
   
    bonLivraisonWindow(com.choranet.gesticom.BonLivraisonWindow, ref("bonCommandeService"), ref("bonLivraisonService"), ref("parametrageService")) { bean ->
        bean.scope = "prototype"
        bean.autowire = "byName"
    }
    
    categorieProduitWindow(com.choranet.gesticom.CategorieProduitWindow) { bean ->
        bean.scope = "prototype"
        bean.autowire = "byName"
    }
    
    mouvementStockWindow(com.choranet.gesticom.MouvementStockWindow, ref("securitestockService"), ref("parametrageService")) { bean ->
        bean.scope = "prototype"
        bean.autowire = "byName"
    }
    
    produitWindow(com.choranet.gesticom.ProduitWindow) { bean ->
        bean.scope = "prototype"
        bean.autowire = "byName"
    }
 
    securitestockWindow(com.choranet.gesticom.SecuritestockWindow, ref("parametrageService")) { bean ->
        bean.scope = "prototype"
        bean.autowire = "byName"
    }
    homeWindow(com.choranet.gesticom.HomeWindow, ref("parametrageService"), ref("securitestockService")) { bean ->
        bean.scope = "prototype"
        bean.autowire = "byName"
    }
    
    bonCommandeWindow(com.choranet.gesticom.BonCommandeWindow, ref("bonCommandeService")) { bean ->
        bean.scope = "prototype"
        bean.autowire = "byName"
    }
    
    inventaireWindow(com.choranet.gesticom.InventaireWindow, ref("inventaireService"), ref("parametrageService")) { bean ->
        bean.scope = "prototype"
        bean.autowire = "byName"
    }
    
}
