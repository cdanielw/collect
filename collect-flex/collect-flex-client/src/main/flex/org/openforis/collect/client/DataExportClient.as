package org.openforis.collect.client {
	import mx.controls.Alert;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.remoting.Operation;
	
	/**
	 * 
	 * @author S. Ricci
	 * */
	public class DataExportClient extends AbstractClient {
		
		private var _getStateOperation:Operation;
		private var _exportOperation:Operation;
		private var _cancelOperation:Operation;
		
		public function DataExportClient() {
			super("dataExportService");
			
			this._getStateOperation = getOperation("getState");
			this._exportOperation = getOperation("export");
			this._cancelOperation = getOperation("cancel");
		}
		
		public function getState(responder:IResponder):void {
			var token:AsyncToken = this._getStateOperation.send();
			token.addResponder(responder);
		}
		
		public function export(responder:IResponder, rootEntityName:String, entityId:int, stepNumber:int):void {
			var token:AsyncToken = this._exportOperation.send(rootEntityName, entityId, stepNumber);
			token.addResponder(responder);
		}
		
		public function cancel(responder:IResponder):void {
			var token:AsyncToken = this._cancelOperation.send();
			token.addResponder(responder);
		}
		
	}
}